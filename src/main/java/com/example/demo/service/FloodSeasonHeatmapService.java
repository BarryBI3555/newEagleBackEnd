package com.example.demo.service;

import com.example.demo.entity.HeatData;
import java.util.List;

public interface FloodSeasonHeatmapService {
    List<HeatData> getHeatData(String date);
    List<HeatData> getAllHeatData();
}