package com.example.demo.controller;


import com.example.demo.entity.Result;
import com.example.demo.entity.UserLocation;
import com.example.demo.service.LocationProgressCacheService;
import com.example.demo.service.UserLocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// @CrossOrigin("*")
@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class LocationController {

    private static final Logger log = LoggerFactory.getLogger(LocationController.class);

    @Autowired
    private UserLocationService userLocationService;

    @Autowired
    private LocationProgressCacheService locationProgressCacheService;

    @GetMapping("/locations")
    public Result<List<UserLocation>> getLocations() {
        return Result.success(userLocationService.getAllLocations());
    }

    // ====================== 获取指定日期每个用户最新位置 ======================
    @GetMapping("/locations/latest")
    public Result<List<UserLocation>> getLatestLocations(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String groupscode,
            @RequestParam(required = false) String keyword
    ) {
        try {
            LocalDate queryDate = (date == null || date.isEmpty()) ? LocalDate.now() : LocalDate.parse(date);
            return Result.success(userLocationService.getLatestLocationsByDate(queryDate, groupscode, keyword));
        } catch (DateTimeParseException e) {
            return Result.error("日期格式错误，应为 yyyy-MM-dd");
        } catch (Exception e) {
            log.error("获取最新位置失败", e);
            return Result.error("获取最新位置失败: " + e.getMessage(), e);
        }
    }

    // ====================== 获取指定日期用户当天轨迹 ======================
    @GetMapping("/locations/user/{usercode}")
    public Result<List<UserLocation>> getTodayLocationsByUser(
            @PathVariable String usercode,
            @RequestParam(required = false) String date
    ) {
        try {
            LocalDate queryDate = (date == null || date.isEmpty()) ? LocalDate.now() : LocalDate.parse(date);
            return Result.success(userLocationService.getUserLocationsByDate(usercode, queryDate));
        } catch (DateTimeParseException e) {
            return Result.error("日期格式错误，应为 yyyy-MM-dd");
        } catch (Exception e) {
            log.error("获取用户轨迹失败", e);
            return Result.error("获取用户轨迹失败: " + e.getMessage(), e);
        }
    }

    // ====================== 获取地址解析进度 ======================
    @GetMapping("/locations/latest/progress")
    public Result<Map<String, Object>> getLocationProgress(
            @RequestParam(required = false) String date
    ) {
        try {
            LocalDate queryDate = (date == null || date.isEmpty()) ? LocalDate.now() : LocalDate.parse(date);

            Map<String, Object> data = new HashMap<>();
            data.put("processing", locationProgressCacheService.isProcessing(queryDate));
            data.put("progress", locationProgressCacheService.getProgress(queryDate));
            data.put("complete", locationProgressCacheService.isComplete(queryDate));

            return Result.success(data);
        } catch (DateTimeParseException e) {
            return Result.error("日期格式错误，应为 yyyy-MM-dd");
        } catch (Exception e) {
            log.error("获取地址解析进度失败", e);
            return Result.error("获取地址解析进度失败: " + e.getMessage(), e);
        }
    }

}