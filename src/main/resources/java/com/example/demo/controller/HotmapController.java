package com.example.demo.controller;

import com.example.demo.entity.HeatData;
import com.example.demo.entity.StatsCardData;
import com.example.demo.service.HeatDataCacheService;
import com.example.demo.service.HotmapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HotmapController {

    @Autowired
    private HotmapService hotmapService;
    
    @Autowired
    private HeatDataCacheService cacheService;

    @GetMapping("/hotmap")
    public List<HeatData> getHotmapData(
            @RequestParam(required = false) String date
    ) {
        LocalDate queryDate = date == null ? LocalDate.now() : LocalDate.parse(date);
        return hotmapService.getHeatData(queryDate);
    }

    @GetMapping("/statsCardsData")
    public List<StatsCardData> getStatsCardsData(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        LocalDate queryDate = date == null ? LocalDate.now().minusDays(1) : date;
        return hotmapService.getStatsCardsData(queryDate);
    }
    
    /**
     * 获取热力图数据解析进度
     */
    @GetMapping("/hotmap/progress")
    public Map<String, Object> getHotmapProgress(
            @RequestParam(required = false) String date
    ) {
        LocalDate queryDate = date == null ? LocalDate.now() : LocalDate.parse(date);
        
        Map<String, Object> result = new HashMap<>();
        result.put("processing", cacheService.isProcessing(queryDate));
        result.put("progress", cacheService.getProgress(queryDate));
        result.put("complete", cacheService.isCacheComplete(queryDate));
        
        // 返回当前缓存的数据量
        List<HeatData> cachedData = cacheService.getCachedHeatData(queryDate);
        result.put("cachedCount", cachedData.size());
        
        return result;
    }
    
    /**
     * 清除指定日期的缓存（触发重新解析）
     */
    @GetMapping("/hotmap/clearCache")
    public Map<String, Object> clearHotmapCache(
            @RequestParam(required = false) String date
    ) {
        LocalDate queryDate = date == null ? LocalDate.now() : LocalDate.parse(date);
        cacheService.clearCache(queryDate);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "缓存已清除");
        
        return result;
    }
}