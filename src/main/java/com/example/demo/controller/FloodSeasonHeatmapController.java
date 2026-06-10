package com.example.demo.controller;

import com.example.demo.entity.HeatData;
import com.example.demo.entity.Result;
import com.example.demo.service.FloodSeasonHeatmapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/rain")
public class FloodSeasonHeatmapController {

    @Autowired
    private FloodSeasonHeatmapService floodSeasonHeatmapService;

    @GetMapping("/hotmap")
    public Result<List<HeatData>> getHotmapData(
            @RequestParam(required = false) String date
    ) {
        try {
            List<HeatData> data;
            if (date == null || date.isEmpty()) {
                data = floodSeasonHeatmapService.getAllHeatData();
            } else {
                data = floodSeasonHeatmapService.getHeatData(date);
            }
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("获取热力图数据失败: " + e.getMessage());
        }
    }
}