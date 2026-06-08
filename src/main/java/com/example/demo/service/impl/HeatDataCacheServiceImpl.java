package com.example.demo.service.impl;

import com.example.demo.entity.HeatData;
import com.example.demo.service.HeatDataCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        return new ArrayList<>(entry.data);
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
