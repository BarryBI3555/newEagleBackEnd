package com.example.demo.service.impl;

import com.example.demo.entity.HeatData;
import com.example.demo.service.HeatDataCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 热力图数据缓存服务实现（重构版：单 Map + LRU + TTL）
 *
 * <h3>改进点</h3>
 * <ul>
 *   <li><b>单 Map</b>：4 张 Map (data/processing/progress/complete) → 1 张 Map&lt;LocalDate, CacheEntry&gt;，
 *       避免"忘记清一个 Map 导致状态不一致"的问题</li>
 *   <li><b>LRU</b>：超过 MAX_CACHE_SIZE 时按 lastAccessTime 淘汰最早的</li>
 *   <li><b>TTL</b>：超过 7 天未访问的条目会被定时清理</li>
 *   <li><b>O(1) count</b>：{@link #getCachedCount} 不复制列表，直接返回 size</li>
 * </ul>
 */
@Service
public class HeatDataCacheServiceImpl implements HeatDataCacheService {

    private static final Logger logger = LoggerFactory.getLogger(HeatDataCacheServiceImpl.class);

    /** 缓存最大日期数（按访问时间淘汰） */
    private static final int MAX_CACHE_SIZE = 100;
    /** TTL：7 天未访问则过期 */
    private static final long TTL_MILLIS = 7L * 24 * 60 * 60 * 1000;
    /** 清理任务执行间隔：每 10 分钟跑一次 */
    private static final long CLEANUP_INTERVAL_MS = 10L * 60 * 1000;

    /** 单区域占总案件 > 95% 强制标 stat */
    private static final double DOMINANT_REGION_RATIO = 0.95;

    @Value("${heatmap.region.step-deg:1.0}")
    private double stepDeg;
    @Value("${heatmap.region.sigma:1.3}")
    private double sigma;
    @Value("${heatmap.region.watch-sigma:1.0}")
    private double watchSigma;
    @Value("${heatmap.region.k-min:1}")
    private int kMin;
    @Value("${heatmap.region.k-max:2}")
    private int kMax;
    @Value("${heatmap.region.use-robust:true}")
    private boolean useRobust;
    @Value("${heatmap.region.dispersion-eps:1e-6}")
    private double dispersionEps;

    /** 单 Map 替代原来 4 张 Map */
    private final Map<LocalDate, CacheEntry> cache = new ConcurrentHashMap<>();

    @Override
    public List<HeatData> getCachedHeatData(LocalDate date) {
        CacheEntry entry = cache.get(date);
        if (entry == null) {
            return new ArrayList<>();
        }
        entry.touch();
        // 防御性拷贝，避免外部修改内部状态
        List<HeatData> copy = new ArrayList<>(entry.data);
        // 对拷贝标记异常后返回（缓存本身保持原始数据不变）
        markRegionAbnormal(copy);
        return copy;
    }

    /**
     * 基于"区域聚合 + median+MAD 稳健基线"标记异常点
     *
     * 流程（见 spec §4）：
     *   Step 1: 按 (regionLng, regionLat) 聚合 → regionTotal
     *   Step 2: 边界检查（非空区域 < 2 → 全 normal；MAD = 0 → 走 top-K 兜底）
     *   Step 3: 计算 modified zscore = 0.6745 * (x - median) / MAD
     *   Step 4: 初步判定 severity（stat / watch / normal）
     *   Step 5: K_min 补足 / K_max 截断
     *   Step 6: 点级传播 + 向后兼容 abnormal 字段
     */
    void markRegionAbnormal(List<HeatData> dataList) {
        if (dataList == null || dataList.isEmpty()) return;

        // Step 1: 区域聚合
        Map<RegionKey, int[]> regionStats = new HashMap<>();  // [total, pointCount]
        for (HeatData p : dataList) {
            if (p.getLng() == null || p.getLat() == null) continue;
            RegionKey k = regionOf(p.getLng(), p.getLat());
            int[] stat = regionStats.computeIfAbsent(k, x -> new int[2]);
            stat[0] += (p.getCount() != null ? p.getCount() : 0);
            stat[1] += 1;
        }
        if (regionStats.isEmpty()) {
            markAllNormal(dataList);
            return;
        }

        // Step 2a: 非空区域 < 2 → 全部 normal
        List<Integer> nonEmptyTotals = new ArrayList<>();
        for (int[] s : regionStats.values()) {
            if (s[0] > 0) nonEmptyTotals.add(s[0]);
        }
        if (nonEmptyTotals.size() < 2) {
            markAllNormal(dataList);
            return;
        }

        // 全局总数 + 95% 主导检测
        long globalTotal = 0;
        for (int[] s : regionStats.values()) globalTotal += s[0];
        RegionKey dominant = null;
        for (Map.Entry<RegionKey, int[]> e : regionStats.entrySet()) {
            if (e.getValue()[0] > 0
                && (double) e.getValue()[0] / globalTotal > DOMINANT_REGION_RATIO) {
                dominant = e.getKey();
            }
        }

        // Step 2b: 计算 median + MAD（仅对非空区域）
        nonEmptyTotals.sort(Integer::compareTo);
        int median = nonEmptyTotals.get(nonEmptyTotals.size() / 2);
        List<Integer> deviations = new ArrayList<>();
        for (int t : nonEmptyTotals) deviations.add(Math.abs(t - median));
        deviations.sort(Integer::compareTo);
        double mad = deviations.get(deviations.size() / 2);

        Map<RegionKey, String> severity = new HashMap<>();
        Map<RegionKey, Double> zScore = new HashMap<>();

        if (mad > 0) {
            // Step 3+4: modified zscore + 初步判定
            for (Map.Entry<RegionKey, int[]> e : regionStats.entrySet()) {
                RegionKey k = e.getKey();
                int total = e.getValue()[0];
                if (total == 0) {
                    severity.put(k, "normal");
                    zScore.put(k, 0.0);
                    continue;
                }
                double z = 0.6745 * (total - median) / mad;
                zScore.put(k, z);
                double absZ = Math.abs(z);
                if (absZ > sigma) {
                    severity.put(k, "stat");
                } else if (absZ > watchSigma) {
                    severity.put(k, "watch");
                } else {
                    severity.put(k, "normal");
                }
            }
        } else {
            // MAD = 0：所有 region_total 相等 → 全部 normal（后续走 K_min 兜底）
            for (RegionKey k : regionStats.keySet()) {
                severity.put(k, "normal");
                zScore.put(k, 0.0);
            }
        }

        // 95% 主导区域强制 stat
        if (dominant != null) severity.put(dominant, "stat");

        // Step 5a: K_min 补足
        long statCount = severity.values().stream().filter("stat"::equals).count();
        if (statCount < kMin) {
            int needed = (int) (kMin - statCount);
            // 候选：normal + watch 区域，按 regionTotal 降序
            List<Map.Entry<RegionKey, int[]>> candidates = new ArrayList<>();
            for (Map.Entry<RegionKey, int[]> e : regionStats.entrySet()) {
                String sev = severity.get(e.getKey());
                if ("stat".equals(sev)) continue;  // 已 stat 跳过
                candidates.add(e);
            }
            candidates.sort((a, b) -> b.getValue()[0] - a.getValue()[0]);
            for (int i = 0; i < Math.min(needed, candidates.size()); i++) {
                // watch 区域保持 watch，不升级 topk（spec §4.5 设计）
                RegionKey k = candidates.get(i).getKey();
                if (!"watch".equals(severity.get(k))) {
                    severity.put(k, "topk");
                }
            }
        }

        // Step 5b: K_max 截断（优先丢 topk，按 |z| 升序）
        List<RegionKey> actionable = new ArrayList<>();
        for (Map.Entry<RegionKey, String> e : severity.entrySet()) {
            if ("stat".equals(e.getValue()) || "topk".equals(e.getValue())) {
                actionable.add(e.getKey());
            }
        }
        if (actionable.size() > kMax) {
            int excess = actionable.size() - kMax;
            List<RegionKey> topkList = new ArrayList<>();
            for (RegionKey k : actionable) {
                if ("topk".equals(severity.get(k))) topkList.add(k);
            }
            topkList.sort((a, b) -> Double.compare(
                Math.abs(zScore.get(a)), Math.abs(zScore.get(b))));
            for (int i = 0; i < Math.min(excess, topkList.size()); i++) {
                severity.put(topkList.get(i), "normal");
            }
        }

        // Step 6: 点级传播 + 向后兼容 abnormal
        for (HeatData p : dataList) {
            if (p.getLng() == null || p.getLat() == null) continue;
            RegionKey k = regionOf(p.getLng(), p.getLat());
            String sev = severity.getOrDefault(k, "normal");
            p.setSeverity(sev);
            p.setAbnormal(!"normal".equals(sev));
        }

        long statN = severity.values().stream().filter("stat"::equals).count();
        long topkN = severity.values().stream().filter("topk"::equals).count();
        long watchN = severity.values().stream().filter("watch"::equals).count();
        long normalN = severity.values().stream().filter("normal"::equals).count();
        logger.info("[markRegionAbnormal] 区域数={}, median={}, MAD={}, severity分布: stat={}, topk={}, watch={}, normal={}",
            regionStats.size(), median, mad, statN, topkN, watchN, normalN);
    }

    /**
     * 区域 key：(floor(lng/step)*step, floor(lat/step)*step)
     * 用 static class + 手写 equals/hashCode（项目 Java 8，不支持 record）
     */
    private RegionKey regionOf(Double lng, Double lat) {
        double rLng = Math.floor(lng / stepDeg) * stepDeg;
        double rLat = Math.floor(lat / stepDeg) * stepDeg;
        return new RegionKey(rLng, rLat);
    }

    private void markAllNormal(List<HeatData> dataList) {
        for (HeatData p : dataList) {
            p.setSeverity("normal");
            p.setAbnormal(false);
        }
    }

    /**
     * 区域 key：半开区间 [a, a+step) 避免边界重复归类
     */
    private static class RegionKey {
        final double lng;
        final double lat;

        RegionKey(double lng, double lat) {
            this.lng = lng;
            this.lat = lat;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RegionKey)) return false;
            RegionKey that = (RegionKey) o;
            return Double.compare(that.lng, lng) == 0
                && Double.compare(that.lat, lat) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(lng, lat);
        }

        @Override
        public String toString() {
            return "(" + lng + "," + lat + ")";
        }
    }

    /**
     * 旧实现：基于 Poisson / Negative Binomial Pearson 残差
     * 已弃用（spec §十一回滚方案保留），保留以便回滚时切换
     */
    @Deprecated
    private void markAbnormalOnList(List<HeatData> dataList) {
        if (dataList == null || dataList.isEmpty()) return;
        int n = dataList.size();
        List<Integer> counts = new ArrayList<>();
        for (HeatData d : dataList) {
            if (d.getCount() != null) counts.add(d.getCount());
        }
        if (counts.isEmpty()) return;

        // Step 1: 计算全局均值 λ̂
        double lambda = counts.stream().mapToInt(Integer::intValue).average().orElse(0);
        if (lambda < 1e-6) return;

        // Step 2: 计算方差和离散度，判断是否过离散
        double variance = counts.stream()
                .mapToDouble(v -> Math.pow(v - lambda, 2)).sum() / n;
        double dispersion = variance / lambda; // Var/Mean 比值

        // Step 3: 估算负二项过离散参数 θ
        // θ = λ / (dispersion - 1)，dispersion <= 1 时无需过离散（用泊松）
        double theta = Double.MAX_VALUE;
        boolean useNegBin = false;
        if (dispersion > 1.0 && lambda > 0) {
            theta = lambda / (dispersion - 1.0);
            useNegBin = theta > 0.1; // θ 过小说明严重过离散，但仍可计算
        }

        // Step 4: 计算 Pearson 残差并标记
        long abnormalCount = 0;
        for (HeatData d : dataList) {
            double y = d.getCount() != null ? d.getCount() : 0;
            double residual;
            if (useNegBin) {
                // Negative Binomial Pearson 残差
                double nbVariance = lambda + (lambda * lambda) / theta;
                residual = (y - lambda) / Math.sqrt(nbVariance);
            } else {
                // Poisson Pearson 残差
                residual = (y - lambda) / Math.sqrt(lambda);
            }
            d.setAbnormal(Math.abs(residual) > 2.0);
            if (Math.abs(residual) > 2.0) abnormalCount++;
        }

        logger.info("[markAbnormalOnList] 数据量={}, 均值={}, 方差={}, 离散度={}, 模型={}, 异常点数={}",
                n, String.format("%.3f", lambda), String.format("%.3f", variance),
                String.format("%.3f", dispersion), useNegBin ? "NegativeBinomial" : "Poisson", abnormalCount);
    }

    @Override
    public int getCachedCount(LocalDate date) {
        CacheEntry entry = cache.get(date);
        if (entry == null) {
            return 0;
        }
        entry.touch();
        return entry.data.size();
    }

    @Override
    public void cacheHeatData(LocalDate date, List<HeatData> data) {
        CacheEntry entry = cache.computeIfAbsent(date, k -> new CacheEntry());
        entry.data = new ArrayList<>(data);
        entry.touch();
    }

    @Override
    public void clearCache(LocalDate date) {
        cache.remove(date);
    }

    @Override
    public boolean isProcessing(LocalDate date) {
        CacheEntry entry = cache.get(date);
        return entry != null && entry.processing;
    }

    @Override
    public void setProcessing(LocalDate date, boolean processing) {
        CacheEntry entry = cache.computeIfAbsent(date, k -> new CacheEntry());
        entry.processing = processing;
        if (!processing) {
            entry.progress = 100;
        }
        entry.touch();
    }

    @Override
    public int getProgress(LocalDate date) {
        CacheEntry entry = cache.get(date);
        return entry == null ? 0 : entry.progress;
    }

    @Override
    public void setProgress(LocalDate date, int progress) {
        CacheEntry entry = cache.computeIfAbsent(date, k -> new CacheEntry());
        entry.progress = Math.min(100, Math.max(0, progress));
        entry.touch();
    }

    @Override
    public boolean isCacheComplete(LocalDate date) {
        CacheEntry entry = cache.get(date);
        return entry != null && entry.complete;
    }

    @Override
    public void setCacheComplete(LocalDate date) {
        CacheEntry entry = cache.computeIfAbsent(date, k -> new CacheEntry());
        entry.complete = true;
        entry.processing = false;
        entry.progress = 100;
        entry.touch();
    }

    @Override
    public void mergeHeatData(LocalDate date, List<HeatData> newData) {
        CacheEntry entry = cache.computeIfAbsent(date, k -> new CacheEntry());
        if (newData == null || newData.isEmpty()) {
            return;
        }

        // 用临时 Map 去重（每次 merge 都重建，n 一般较小可接受）
        Map<String, HeatData> existingMap = new HashMap<>();
        for (HeatData hd : entry.data) {
            String key = String.format("%.3f,%.3f", hd.getLng(), hd.getLat());
            existingMap.put(key, hd);
        }

        for (HeatData newHd : newData) {
            String key = String.format("%.3f,%.3f", newHd.getLng(), newHd.getLat());
            HeatData existing = existingMap.get(key);
            if (existing != null) {
                existing.setCount(existing.getCount() + newHd.getCount());
            } else {
                entry.data.add(newHd);
                existingMap.put(key, newHd);
            }
        }
        entry.touch();
    }

    /**
     * 定期清理过期 / 超量条目
     * <p>每 10 分钟一次；过期（&gt; 7 天未访问）+ 超量（&gt; 100 条）→ 淘汰最旧</p>
     */
    @Scheduled(fixedRate = CLEANUP_INTERVAL_MS)
    public void cleanupExpiredAndOversized() {
        long now = System.currentTimeMillis();
        int sizeBefore = cache.size();

        // Step 1: 按 TTL 淘汰
        cache.entrySet().removeIf(e -> now - e.getValue().lastAccessTime > TTL_MILLIS);

        // Step 2: 按 LRU 淘汰（仅在超量时）
        if (cache.size() > MAX_CACHE_SIZE) {
            int evictCount = cache.size() - MAX_CACHE_SIZE;
            cache.entrySet().stream()
                    .sorted(Comparator.comparingLong(e -> e.getValue().lastAccessTime))
                    .limit(evictCount)
                    .map(Map.Entry::getKey)
                    .forEach(cache::remove);
        }

        int sizeAfter = cache.size();
        if (sizeBefore != sizeAfter) {
            logger.debug("热力图缓存清理: {} → {} (MAX_SIZE={}, TTL={}d)",
                    sizeBefore, sizeAfter, MAX_CACHE_SIZE, TTL_MILLIS / (24 * 60 * 60 * 1000));
        }
    }

    public int size() {
        return cache.size();
    }

    /**
     * 缓存条目：打包 data / 状态标志 / 进度 / 上次访问时间
     * <p>lastAccessTime 用 volatile，保证多线程下及时可见（写入远多于读，O(1) 缓存行更新）</p>
     */
    private static class CacheEntry {
        volatile List<HeatData> data = new ArrayList<>();
        volatile boolean processing;
        volatile int progress;
        volatile boolean complete;
        volatile long lastAccessTime = System.currentTimeMillis();

        void touch() {
            this.lastAccessTime = System.currentTimeMillis();
        }
    }
}
