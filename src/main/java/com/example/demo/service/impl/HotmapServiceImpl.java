package com.example.demo.service.impl;

import com.example.demo.entity.AcdCwTbCll;
import com.example.demo.entity.GeocodeResult;
import com.example.demo.entity.HeatData;
import com.example.demo.entity.PrplCheckTask;
import com.example.demo.entity.StatsCardData;
import com.example.demo.mapper.AcdCwTbCllMapper;
import com.example.demo.mapper.PrplCheckTaskMapper;
import com.example.demo.service.HeatDataCacheService;
import com.example.demo.service.HotmapService;
import com.example.demo.util.LocationAddressConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HotmapServiceImpl implements HotmapService {

    private static final Logger logger = LoggerFactory.getLogger(HotmapServiceImpl.class);
    
    // 最大返回的坐标点数量
    private static final int MAX_HEAT_POINTS = 10000;

    @Autowired
    private PrplCheckTaskMapper prplCheckTaskMapper;

    @Autowired
    private AcdCwTbCllMapper acdCwTbCllMapper;

    @Autowired
    private LocationAddressConverter addressConverter;
    
    @Autowired
    private HeatDataCacheService cacheService;
    
    @Value("${app.heatmap.enable-geocode:true}")
    private boolean enableGeocode;

    @Override
    public List<HeatData> getHeatData(LocalDate date) {
        String dateStr = date.toString();
        logger.info("开始获取热力图数据: date={}", dateStr);
        
        // 先尝试从缓存获取数据
        List<HeatData> cachedData = cacheService.getCachedHeatData(date);
        
        // 获取数据库聚合查询的数据
        List<Map<String, Object>> dbHeatData = prplCheckTaskMapper.getHeatDataByDate(dateStr);
        List<HeatData> dbResult = convertDbHeatData(dbHeatData);
        
        // 如果缓存为空，使用数据库数据初始化缓存
        if (cachedData.isEmpty()) {
            cacheService.cacheHeatData(date, dbResult);
            cachedData = new ArrayList<>(dbResult);
        } else {
            // 合并数据库数据到缓存（确保缓存包含最新的数据库数据）
            cacheService.mergeHeatData(date, dbResult);
            cachedData = cacheService.getCachedHeatData(date);
        }
        
        logger.info("数据库查询到 {} 个坐标点，缓存中共有 {} 个坐标点", dbResult.size(), cachedData.size());
        
        // 检查是否需要异步解析地址
        if (enableGeocode && !cacheService.isProcessing(date) && !cacheService.isCacheComplete(date)) {
            // 检查是否有需要解析的地址数据
            int totalCount = prplCheckTaskMapper.countTasksByDate(dateStr);
            logger.info("检查是否需要异步解析: 总数据量={}, 缓存数据量={}", totalCount, cachedData.size());
            
            // 如果数据库数据量大于缓存数据量，说明有地址需要解析
            if (totalCount > cachedData.size()) {
                logger.info("启动异步地址解析任务");
                cacheService.setProcessing(date, true);
                cacheService.setProgress(date, 0);
                asyncGeocodeAddresses(date, dateStr);
            } else {
                // 数据量匹配，标记为已完成
                cacheService.setCacheComplete(date);
            }
        }
        
        // 返回当前缓存的数据（限制数量）
        if (cachedData.size() > MAX_HEAT_POINTS) {
            logger.warn("热力图数据量超过最大限制 {}，已截断", MAX_HEAT_POINTS);
            return cachedData.subList(0, MAX_HEAT_POINTS);
        }
        
        return cachedData;
    }
    
    /**
     * 异步解析地址
     */
    @Async
    public void asyncGeocodeAddresses(LocalDate date, String dateStr) {
        logger.info("开始异步地址解析任务: date={}", dateStr);
        
        try {
            // 获取所有需要解析地址的数据（经纬度为空或无效的记录）
            List<PrplCheckTask> tasks = prplCheckTaskMapper.getAllTasksByDate(dateStr);
            
            if (tasks == null || tasks.isEmpty()) {
                logger.info("没有需要解析的地址数据");
                cacheService.setCacheComplete(date);
                return;
            }
            
            // 筛选出需要解析的记录
            List<PrplCheckTask> tasksToGeocode = new ArrayList<>();
            for (PrplCheckTask task : tasks) {
                Double lng = task.getChecklongitude();
                Double lat = task.getChecklatitude();
                String checksite = task.getChecksite();
                
                // 经纬度为空或无效，且有地址信息
                if ((lng == null || lat == null || Double.isNaN(lng) || Double.isNaN(lat) 
                        || lng == 0 || lat == 0) && checksite != null && !checksite.trim().isEmpty()) {
                    tasksToGeocode.add(task);
                }
            }
            
            logger.info("需要解析的地址数量: {}", tasksToGeocode.size());
            
            if (tasksToGeocode.isEmpty()) {
                cacheService.setCacheComplete(date);
                return;
            }
            
            int total = tasksToGeocode.size();
            int processed = 0;
            int successCount = 0;
            int failCount = 0;
            
            List<HeatData> geocodeResults = new ArrayList<>();
            
            for (PrplCheckTask task : tasksToGeocode) {
                try {
                    GeocodeResult result = addressConverter.geocode(task.getChecksite());
                    if (result != null && result.getStatus() == 0 && result.getResult() != null
                            && result.getResult().getLocation() != null) {
                        
                        Double lng = result.getResult().getLocation().getLng();
                        Double lat = result.getResult().getLocation().getLat();
                        
                        HeatData heatData = new HeatData();
                        heatData.setLng(lng);
                        heatData.setLat(lat);
                        heatData.setCount(1);
                        geocodeResults.add(heatData);
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    logger.debug("地址解析失败: {}, {}", task.getChecksite(), e.getMessage());
                    failCount++;
                }
                
                processed++;
                
                // 更新进度（每处理10条更新一次）
                if (processed % 10 == 0 || processed == total) {
                    int progress = (int) ((processed * 100.0) / total);
                    cacheService.setProgress(date, progress);
                    
                    // 每处理50条合并一次结果到缓存
                    if (geocodeResults.size() >= 50) {
                        cacheService.mergeHeatData(date, geocodeResults);
                        geocodeResults.clear();
                    }
                }
                
                // 添加小延迟，避免频繁调用外部API
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            // 合并剩余的解析结果
            if (!geocodeResults.isEmpty()) {
                cacheService.mergeHeatData(date, geocodeResults);
            }
            
            logger.info("异步地址解析完成: 总数={}, 成功={}, 失败={}", total, successCount, failCount);
            
            // 标记解析完成
            cacheService.setProgress(date, 100);
            cacheService.setCacheComplete(date);
            
        } catch (Exception e) {
            logger.error("异步地址解析任务失败: {}", e.getMessage(), e);
            cacheService.setProcessing(date, false);
        }
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