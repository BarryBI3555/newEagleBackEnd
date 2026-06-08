package com.example.demo.controller;

import com.example.demo.entity.HeatData;
import com.example.demo.entity.Result;
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
import java.time.format.DateTimeParseException;
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
    public Result<List<HeatData>> getHotmapData(
            @RequestParam(required = false) String date
    ) {
        try {
            LocalDate queryDate = (date == null || date.isEmpty()) ? LocalDate.now() : LocalDate.parse(date);
            return Result.success(hotmapService.getHeatData(queryDate));
        } catch (DateTimeParseException e) {
            return Result.error("日期格式错误，应为 yyyy-MM-dd");
        } catch (Exception e) {
            return Result.error("获取热力图数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/statsCardsData")
    public Result<List<StatsCardData>> getStatsCardsData(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        try {
            LocalDate queryDate = date == null ? LocalDate.now().minusDays(1) : date;
            return Result.success(hotmapService.getStatsCardsData(queryDate));
        } catch (Exception e) {
            return Result.error("获取统计卡片数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取热力图数据解析进度
     */
    @GetMapping("/hotmap/progress")
    public Result<Map<String, Object>> getHotmapProgress(
            @RequestParam(required = false) String date
    ) {
        try {
            LocalDate queryDate = (date == null || date.isEmpty()) ? LocalDate.now() : LocalDate.parse(date);

            Map<String, Object> data = new HashMap<>();
            data.put("processing", cacheService.isProcessing(queryDate));
            data.put("progress", cacheService.getProgress(queryDate));
            data.put("complete", cacheService.isCacheComplete(queryDate));
            // 用 O(1) 的 getCachedCount 替代原本 O(n) 的 getCachedHeatData().size()（不再复制整份数据）
            data.put("cachedCount", cacheService.getCachedCount(queryDate));

            return Result.success(data);
        } catch (DateTimeParseException e) {
            return Result.error("日期格式错误，应为 yyyy-MM-dd");
        } catch (Exception e) {
            return Result.error("获取热力图进度失败: " + e.getMessage());
        }
    }

    /**
     * 清除指定日期的缓存（触发重新解析）
     */
    @GetMapping("/hotmap/clearCache")
    public Result<Map<String, Object>> clearHotmapCache(
            @RequestParam(required = false) String date
    ) {
        try {
            LocalDate queryDate = (date == null || date.isEmpty()) ? LocalDate.now() : LocalDate.parse(date);
            cacheService.clearCache(queryDate);

            Map<String, Object> data = new HashMap<>();
            data.put("success", true);
            data.put("message", "缓存已清除");

            return Result.success(data);
        } catch (DateTimeParseException e) {
            return Result.error("日期格式错误，应为 yyyy-MM-dd");
        } catch (Exception e) {
            return Result.error("清除热力图缓存失败: " + e.getMessage());
        }
    }
}