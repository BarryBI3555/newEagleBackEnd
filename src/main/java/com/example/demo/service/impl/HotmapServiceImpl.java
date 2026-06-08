package com.example.demo.service.impl;

import com.example.demo.entity.AcdCwTbCll;
import com.example.demo.entity.HeatData;
import com.example.demo.entity.StatsCardData;
import com.example.demo.mapper.AcdCwTbCllMapper;
import com.example.demo.mapper.PrplCheckTaskMapper;
import com.example.demo.service.AsyncGeocodeService;
import com.example.demo.service.HeatDataCacheService;
import com.example.demo.service.HotmapService;
import com.example.demo.util.GeocodeScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HotmapServiceImpl implements HotmapService {

    private static final Logger logger = LoggerFactory.getLogger(HotmapServiceImpl.class);
    
    // 最大返回的坐标点数量
    private static final int MAX_HEAT_POINTS = 10000;

    // 成都区域经纬度边界
    private static final double CD_MIN_LNG = 102.5;
    private static final double CD_MAX_LNG = 104.9;
    private static final double CD_MIN_LAT = 30.0;
    private static final double CD_MAX_LAT = 31.5;

    @Autowired
    private PrplCheckTaskMapper prplCheckTaskMapper;

    @Autowired
    private AcdCwTbCllMapper acdCwTbCllMapper;

    @Autowired
    private HeatDataCacheService cacheService;

    @Autowired
    private AsyncGeocodeService asyncGeocodeService;

    @Autowired
    private GeocodeScheduler geocodeScheduler;
    
    @Value("${app.heatmap.enable-geocode:true}")
    private boolean enableGeocode;

    @Override
    public List<HeatData> getHeatData(LocalDate date) {
        String dateStr = date.toString();
        logger.info("开始获取热力图数据: date={}", dateStr);

        // ============ 快速路径：缓存已 complete，跳过所有 DB 查询 ============
        // 修复前：每次都查 DB（getHeatDataByDate + countTasksByDate），导致"完成后再查仍慢"
        // 修复后：complete 状态下直接返回内存缓存
        if (cacheService.isCacheComplete(date)) {
            List<HeatData> cachedData = cacheService.getCachedHeatData(date);
            logger.info("命中已 complete 缓存: date={}, 数据量={}（跳过 DB）", dateStr, cachedData.size());
            return filterAndLimit(cachedData);
        }

        // ============ 慢速路径：缓存未 complete，需要查 DB 并可能启动异步解析 ============

        // 读缓存（可能是空 / 不完整 / 正在解析中）
        List<HeatData> cachedData = cacheService.getCachedHeatData(date);

        // 查 DB 拿到当天的聚合坐标
        List<Map<String, Object>> dbHeatData = prplCheckTaskMapper.getHeatDataByDate(dateStr);
        List<HeatData> dbResult = convertDbHeatData(dbHeatData);

        if (cachedData.isEmpty()) {
            // 首次访问：直接以 DB 结果初始化缓存
            cacheService.cacheHeatData(date, dbResult);
            cachedData = new ArrayList<>(dbResult);
        } else {
            // 后续访问但还没 complete：把 DB 新数据合并进缓存（DB 是权威源）
            cacheService.mergeHeatData(date, dbResult);
            cachedData = cacheService.getCachedHeatData(date);
        }

        logger.info("数据库查询到 {} 个坐标点，缓存中共有 {} 个坐标点", dbResult.size(), cachedData.size());

        // 检查是否需要异步解析地址（只有未在解析且未 complete 才进入）
        if (enableGeocode && !cacheService.isProcessing(date) && !cacheService.isCacheComplete(date)) {
            int totalCount = prplCheckTaskMapper.countTasksByDate(dateStr);
            logger.info("检查是否需要异步解析: 总数据量={}, 缓存数据量={}", totalCount, cachedData.size());

            if (totalCount > cachedData.size()) {
                // DB 数据量比缓存多 → 有新地址待解析
                logger.info("启动异步地址解析任务");
                cacheService.setProcessing(date, true);
                cacheService.setProgress(date, 0);
                String taskKey = "hotmap:" + dateStr;
                geocodeScheduler.submit(taskKey, () -> asyncGeocodeService.doGeocodeAddresses(date, dateStr));
            } else {
                // 数据量一致，无需异步解析
                cacheService.setCacheComplete(date);
            }
        }

        return filterAndLimit(cachedData);
    }

    /**
     * 过滤成都区域外的坐标，并按 MAX_HEAT_POINTS 截断
     */
    private List<HeatData> filterAndLimit(List<HeatData> source) {
        List<HeatData> filteredData = new ArrayList<>();
        for (HeatData hd : source) {
            if (hd.getLng() != null && hd.getLat() != null
                    && hd.getLng() >= CD_MIN_LNG && hd.getLng() <= CD_MAX_LNG
                    && hd.getLat() >= CD_MIN_LAT && hd.getLat() <= CD_MAX_LAT) {
                filteredData.add(hd);
            }
        }

        if (filteredData.size() > MAX_HEAT_POINTS) {
            logger.warn("热力图数据量超过最大限制 {}，已截断", MAX_HEAT_POINTS);
            return filteredData.subList(0, MAX_HEAT_POINTS);
        }

        return filteredData;
    }
    
    /**
     * 转换数据库返回的热力数据
     */
    private List<HeatData> convertDbHeatData(List<Map<String, Object>> dbHeatData) {
        List<HeatData> result = new ArrayList<>();
        
        if (dbHeatData == null || dbHeatData.isEmpty()) {
            return result;
        }
        
        for (Map<String, Object> row : dbHeatData) {
            try {
                Double lng = row.get("lng") != null ? ((Number) row.get("lng")).doubleValue() : null;
                Double lat = row.get("lat") != null ? ((Number) row.get("lat")).doubleValue() : null;
                Integer countVal = row.get("count") != null ? ((Number) row.get("count")).intValue() : 1;
                
                if (lng != null && lat != null) {
                    HeatData heatData = new HeatData();
                    heatData.setLng(lng);
                    heatData.setLat(lat);
                    heatData.setCount(countVal);
                    result.add(heatData);
                }
            } catch (Exception e) {
                logger.warn("解析数据库返回的热力数据失败: {}", e.getMessage());
            }
        }
        
        return result;
    }

    @Override
    public List<StatsCardData> getStatsCardsData(LocalDate date){
        List<StatsCardData> result = new ArrayList<>();

        try {
            AcdCwTbCll data = acdCwTbCllMapper.selectByDate(date);

            if (data != null) {
                logger.info("获取统计数据成功: date={}", date);

                result.add(new StatsCardData("新增立案", 
                        data.getXzlDay() != null ? data.getXzlDay() : 0, 
                        "当日新增立案量"));

                result.add(new StatsCardData("已决案件", 
                        data.getYjlDay() != null ? data.getYjlDay() : 0, 
                        "当日已决量"));

                result.add(new StatsCardData("未决案件", 
                        data.getWjl() != null ? data.getWjl() : 0, 
                        "截止统计日期未决量"));
            } else {
                logger.warn("未找到统计数据: date={}", date);
                
                result.add(new StatsCardData("新增立案", 0, "当日新增立案量"));
                result.add(new StatsCardData("已决案件", 0, "当日已决量"));
                result.add(new StatsCardData("未决案件", 0, "截止统计日期未决量"));
            }
        } catch (Exception e) {
            logger.error("获取统计卡片数据失败: date={}, {}", date, e.getMessage());
            
            result.add(new StatsCardData("新增立案", 0, "当日新增立案量"));
            result.add(new StatsCardData("已决案件", 0, "当日已决量"));
            result.add(new StatsCardData("未决案件", 0, "截止统计日期未决量"));
        }

        return result;
    }
}