package com.example.demo.service.impl;

import com.example.demo.config.HeatmapProperties;
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

    @Autowired
    private HeatmapProperties heatmapProperties;

    @Value("${app.heatmap.enable-geocode:true}")
    private boolean enableGeocode;

    @Override
    public List<HeatData> getHeatData(LocalDate date) {
        String dateStr = date.toString();
        logger.info("Start loading heatmap data: date={}", dateStr);

        List<HeatData> cachedData = cacheService.getCachedHeatData(date);

        List<Map<String, Object>> dbHeatData = prplCheckTaskMapper.getHeatDataByDate(dateStr);
        List<HeatData> dbResult = convertDbHeatData(dbHeatData);

        if (cachedData.isEmpty()) {
            cacheService.cacheHeatData(date, dbResult);
            cachedData = new ArrayList<>(dbResult);
        } else {
            cacheService.mergeHeatData(date, dbResult);
            cachedData = cacheService.getCachedHeatData(date);
        }

        logger.info("Loaded {} heat points from database, {} heat points in memory cache",
                dbResult.size(), cachedData.size());

        if (enableGeocode && !cacheService.isProcessing(date) && !cacheService.isCacheComplete(date)) {
            int missingCount = prplCheckTaskMapper.countMissingCoordinateTasksByDate(dateStr);
            logger.info("Checking async geocode requirement: missingCoordinates={}, cachedPoints={}",
                    missingCount, cachedData.size());

            if (missingCount > 0) {
                logger.info("Submit async address geocode task: date={}", dateStr);
                cacheService.setProcessing(date, true);
                cacheService.setProgress(date, 0);
                cacheService.setStats(date, missingCount, 0, 0, 0);
                String taskKey = "hotmap:" + dateStr;
                geocodeScheduler.submit(taskKey, () -> asyncGeocodeService.doGeocodeAddresses(date, dateStr));
            } else {
                cacheService.setCacheComplete(date);
            }
        }

        List<HeatData> filteredData = new ArrayList<>();
        for (HeatData hd : cachedData) {
            if (heatmapProperties.isInRegion(hd.getLng(), hd.getLat())) {
                filteredData.add(hd);
            }
        }

        int maxPoints = heatmapProperties.getMaxPoints();
        if (filteredData.size() > maxPoints) {
            logger.warn("Heatmap point count exceeds maxPoints={}, truncating result", maxPoints);
            return filteredData.subList(0, maxPoints);
        }

        return filteredData;
    }

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
                logger.warn("Failed to convert heatmap row: {}", e.getMessage());
            }
        }

        return result;
    }

    @Override
    public List<StatsCardData> getStatsCardsData(LocalDate date) {
        List<StatsCardData> result = new ArrayList<>();

        try {
            AcdCwTbCll data = acdCwTbCllMapper.selectByDate(date);

            if (data != null) {
                logger.info("Loaded stats card data: date={}", date);
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
                logger.warn("Stats card data not found: date={}", date);
                addEmptyStatsCards(result);
            }
        } catch (Exception e) {
            logger.error("Failed to load stats card data: date={}, {}", date, e.getMessage());
            addEmptyStatsCards(result);
        }

        return result;
    }

    private void addEmptyStatsCards(List<StatsCardData> result) {
        result.add(new StatsCardData("新增立案", 0, "当日新增立案量"));
        result.add(new StatsCardData("已决案件", 0, "当日已决量"));
        result.add(new StatsCardData("未决案件", 0, "截止统计日期未决量"));
    }
}
