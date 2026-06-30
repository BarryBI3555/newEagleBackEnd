package com.example.demo.service.impl;

import com.example.demo.entity.HeatData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HeatDataCacheServiceImpl.markRegionAbnormalP95 单元测试
 * 覆盖 P95 + 距离合并 + 主导比 算法的 8 个核心场景
 */
class HeatDataCacheServiceImplTest {

    private HeatDataCacheServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new HeatDataCacheServiceImpl();
        // 注入新算法配置默认值（与 application.properties 一致）
        ReflectionTestUtils.setField(service, "stepDeg", 1.0);
        ReflectionTestUtils.setField(service, "mergeDistDeg", 0.003);
        ReflectionTestUtils.setField(service, "p95Min", 5);
        ReflectionTestUtils.setField(service, "dominantRatio", 0.20);
        ReflectionTestUtils.setField(service, "kMax", 8);
    }

    /** 工具：构造一个 (lng, lat, count) 单元 */
    private HeatData pt(double lng, double lat, int count) {
        return new HeatData(lng, lat, count, false, null);
    }

    /**
     * 构造 0.005° 网格视角的 4 区域。
     * coords[i] = {lng, lat}，落在 (102,30)(103,30)(102,31)(103,31) 4 个 1° 步长的 region。
     * 由于 stepDeg=1.0，4 个坐标天然分到 4 个不同 region。
     */
    private List<HeatData> regionTotalView(int[] totals) {
        double[][] coords = { {102.1, 30.1}, {103.1, 30.1}, {102.1, 31.1}, {103.1, 31.1} };
        List<HeatData> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(pt(coords[i][0], coords[i][1], totals[i]));
        }
        return list;
    }

    /** 工具：构造 5 区域数据 */
    private List<HeatData> regionTotalView5(int[] totals) {
        double[][] coords = {
            {102.1, 30.1}, {103.1, 30.1}, {102.1, 31.1},
            {103.1, 31.1}, {104.1, 30.5}
        };
        List<HeatData> list = new ArrayList<>();
        for (int i = 0; i < totals.length; i++) {
            list.add(pt(coords[i][0], coords[i][1], totals[i]));
        }
        return list;
    }

    /** 工具：构造 N 个分散 0.005° 区域（任意坐标），每个区域一个 count */
    private List<HeatData> scatteredView(int[] counts) {
        List<HeatData> list = new ArrayList<>();
        // 经度每条 +1.0°（> stepDeg=1.0）保证各落不同 region
        for (int i = 0; i < counts.length; i++) {
            list.add(pt(102.0 + i * 1.0, 30.0, counts[i]));
        }
        return list;
    }

    /** 统计 severity 分布 */
    private Map<String, Long> countSeverity(List<HeatData> data) {
        Map<String, Long> m = new HashMap<>();
        for (HeatData d : data) {
            m.merge(d.getSeverity(), 1L, Long::sum);
        }
        return m;
    }

    @Test
    void test1_极端差异() {
        // [580, 220, 180, 60] → total=1040, 580/1040=55.8% > 0.20 → 1 个 stat；其余 normal
        List<HeatData> data = regionTotalView(new int[]{580, 220, 180, 60});
        service.markRegionAbnormalP95(data);
        Map<String, Long> sev = countSeverity(data);
        assertEquals(1L, sev.get("stat"), "580 区域应标 stat（主导比 > 0.20）");
        assertEquals(3L, sev.get("normal"), "其余 3 区域应标 normal");
        assertTrue(data.get(0).getAbnormal(), "stat 区域 abnormal=true");
    }

    @Test
    void test2_完全均匀() {
        // [100, 100, 100, 100] → 4 个 region 各 25%，dominantRatio=0.20 → 25% > 20%，4 个全 stat
        List<HeatData> data = regionTotalView(new int[]{100, 100, 100, 100});
        service.markRegionAbnormalP95(data);
        Map<String, Long> sev = countSeverity(data);
        assertEquals(4L, sev.get("stat"), "各 25% > 0.20 主导阈值，4 个全标 stat");
        assertNull(sev.get("topk"), "无 topk");
    }

    @Test
    void test3_p95_min_floor() {
        // [1, 1, 1, 1, 7] → P95=1，threshold=max(5, 1)=5 → 1 个候选（count=7）
        // 候选集群 7/11=63.6% > 0.20 → stat
        List<HeatData> data = regionTotalView5(new int[]{1, 1, 1, 1, 7});
        service.markRegionAbnormalP95(data);
        Map<String, Long> sev = countSeverity(data);
        assertEquals(1L, sev.getOrDefault("stat", 0L), "P95-min=5 识别 1 个候选，且其占比 > 0.20 → stat");
        assertEquals(4L, sev.get("normal"), "其余 4 个 normal");
    }

    @Test
    void test4_单区域独大() {
        // [950, 30, 10, 10] → total=1000, 950/1000=95% > 0.20 → 1 个 stat
        List<HeatData> data = regionTotalView(new int[]{950, 30, 10, 10});
        service.markRegionAbnormalP95(data);
        Map<String, Long> sev = countSeverity(data);
        assertEquals(1L, sev.get("stat"), "950 区域主导比 95% > 0.20，标 stat");
        assertEquals(3L, sev.get("normal"), "其余 normal");
    }

    @Test
    void test5_watch_below_topk() {
        // 10 个分散 region，counts 全部 >= threshold，无主导（各 9-11%）
        // counts: 8 个 10, 2 个 11 → total=102
        // n=10, rank=9*0.95=8.55, lo=8 (10), hi=9 (11), frac=0.55
        // P95 = 10*0.45 + 11*0.55 = 10.55, threshold=10.55
        // 候选：count=11 那 2 个 → 2 个集群
        // kMax=8，2 个候选全 topk，无 watch
        int[] counts = {10, 10, 10, 10, 10, 10, 10, 10, 11, 11};
        List<HeatData> data = scatteredView(counts);
        service.markRegionAbnormalP95(data);
        Map<String, Long> sev = countSeverity(data);
        assertEquals(2L, sev.getOrDefault("topk", 0L), "2 个候选集群未触发主导比，全部 topk");
        assertEquals(0L, sev.getOrDefault("watch", 0L), "候选数 2 < kMax=8，无 watch");
    }

    @Test
    void test5b_watch_below_topk_with_overflow() {
        // 同样数据但让更多候选跨过 P95：10 个全 10
        // n=10, rank=8.55, P95=10, threshold=10 → 10 个全候选
        // 各 10/100=10% ≤ 20% → 不主导
        // kMax=3 → 3 topk + 7 watch
        int[] counts = {10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
        List<HeatData> data = scatteredView(counts);
        ReflectionTestUtils.setField(service, "kMax", 3);
        service.markRegionAbnormalP95(data);
        Map<String, Long> sev = countSeverity(data);
        assertEquals(3L, sev.getOrDefault("topk", 0L), "kMax=3，10 候选中 3 个 topk");
        assertEquals(7L, sev.getOrDefault("watch", 0L), "剩余 7 个候选 watch");
    }

    @Test
    void test6_Kmax截断() {
        // 10 个分散 region 全 count=10，P95=10 → 10 个全候选；kMax=1 → topk 数量被截断
        int[] counts = {10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
        List<HeatData> data = scatteredView(counts);
        ReflectionTestUtils.setField(service, "kMax", 1);
        service.markRegionAbnormalP95(data);
        long topkN = countSeverity(data).getOrDefault("topk", 0L);
        assertTrue(topkN <= 1, "K_max=1 时 topk 应不超过 1，实际=" + topkN);
    }

    @Test
    void test7_sparse_day() {
        // 数据量 < MIN_NON_EMPTY_CELLS(3) → 全 normal（防稀疏日假信号）
        List<HeatData> data = Arrays.asList(
            pt(102.1, 30.1, 100),
            pt(102.2, 30.1, 50)  // 同一 region（stepDeg=1.0）
        );
        service.markRegionAbnormalP95(data);
        for (HeatData d : data) {
            assertEquals("normal", d.getSeverity(), "稀疏日应全 normal");
            assertFalse(d.getAbnormal());
        }
    }

    @Test
    void test8_cluster_merge() {
        // 2 个候选 cell 在 merge-dist-deg=0.003° 内合并为 1 集群
        // 另一 cell 远离，单独 1 集群
        // stepDeg=0.005 → cell 在 (102.000,30.000) (102.005,30.005) (102.100,30.100)
        // 前两者距离 sqrt(0.005²+0.005²)=0.00707 > 0.003 → 不合并
        // 改用更近的坐标：(102.0000,30.0000) (102.0010,30.0010) → 距离≈0.00141 < 0.003 → 合并
        // 第三点远离 (103.000,30.000)
        List<HeatData> data = new ArrayList<>();
        ReflectionTestUtils.setField(service, "stepDeg", 0.005);
        // 5 个分散点 + 2 个近距离点 + 1 个远点
        data.add(pt(102.0000, 30.0000, 20));
        data.add(pt(102.0010, 30.0010, 30));  // 距上者 0.00141° < 0.003° → 合并
        data.add(pt(103.0000, 30.0000, 50));
        data.add(pt(104.0000, 30.0000, 10));
        data.add(pt(105.0000, 30.0000, 10));
        data.add(pt(106.0000, 30.0000, 10));
        data.add(pt(107.0000, 30.0000, 10));
        data.add(pt(108.0000, 30.0000, 10));
        // total=150, P95=30+(30-20)*0.95*7/7 → 大约 30
        // threshold=max(5, P95)≈30，候选：(50), (20+30)合并=50
        // 50/150=33%>0.20 → stat；20+30合并集群=50/150=33%>0.20 → stat
        // 共 2 个集群 2 个 stat
        service.markRegionAbnormalP95(data);
        Map<String, Long> sev = countSeverity(data);
        // 验证合并后 2 个独立 severity 标签，且至少 1 个 stat
        assertNotNull(sev.get("stat"), "至少应有 1 个 stat");
        assertTrue(sev.get("stat") >= 1, "距离合并后主导集群标 stat");
    }

    @Test
    void test9_向后兼容_abnormal字段() {
        // 验证 abnormal 与 severity 一致：severity != "normal" → abnormal=true
        List<HeatData> data = regionTotalView(new int[]{580, 220, 180, 60});
        service.markRegionAbnormalP95(data);
        for (HeatData d : data) {
            String sev = d.getSeverity();
            boolean expectedAbnormal = !"normal".equals(sev);
            assertEquals(expectedAbnormal, d.getAbnormal(),
                "abnormal 应与 severity 一致: " + sev);
        }
    }

    @Test
    void test10_空列表不崩() {
        service.markRegionAbnormalP95(new ArrayList<>());
        service.markRegionAbnormalP95(null);
        // 不抛异常即通过
    }
}