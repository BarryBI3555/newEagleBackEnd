package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HeatmapProperties {

    @Value("${app.heatmap.max-points:10000}")
    private int maxPoints;

    @Value("${app.heatmap.region.min-lng:102.5}")
    private double minLng;

    @Value("${app.heatmap.region.max-lng:104.9}")
    private double maxLng;

    @Value("${app.heatmap.region.min-lat:30.0}")
    private double minLat;

    @Value("${app.heatmap.region.max-lat:31.5}")
    private double maxLat;

    public int getMaxPoints() {
        return maxPoints;
    }

    public boolean isInRegion(Double lng, Double lat) {
        return lng != null && lat != null
                && lng >= minLng && lng <= maxLng
                && lat >= minLat && lat <= maxLat;
    }
}
