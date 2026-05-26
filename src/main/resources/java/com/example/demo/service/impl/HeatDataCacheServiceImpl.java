package com.example.demo.service.impl;

import com.example.demo.entity.HeatData;
import com.example.demo.service.HeatDataCacheService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 热力图数据缓存服务实现
 */
@Service
public class HeatDataCacheServiceImpl implements HeatDataCacheService {

    // 缓存热力图数据
    private final Map<String, List<HeatData>> heatDataCache = new ConcurrentHashMap<>();
    
    // 标记是否正在解析中
    private final Map<String, Boolean> processingFlags = new ConcurrentHashMap<>();
    
    // 解析进度（0-100）
    private final Map<String, Integer> progressMap = new ConcurrentHashMap<>();
    
    // 标记缓存是否完整
    private final Map<String, Boolean> completeFlags = new ConcurrentHashMap<>();

    private String getKey(LocalDate date) {
        return date.toString();
    }

    @Override
    public List<HeatData> getCachedHeatData(LocalDate date) {
        List<HeatData> data = heatDataCache.get(getKey(date));
        return data != null ? new ArrayList<>(data) : new ArrayList<>();
    }

    @Override
    public void cacheHeatData(LocalDate date, List<HeatData> data) {
        heatDataCache.put(getKey(date), new ArrayList<>(data));
    }

    @Override
    public void clearCache(LocalDate date) {
        heatDataCache.remove(getKey(date));
        processingFlags.remove(getKey(date));
        progressMap.remove(getKey(date));
        completeFlags.remove(getKey(date));
    }

    @Override
    public boolean isProcessing(LocalDate date) {
        return Boolean.TRUE.equals(processingFlags.get(getKey(date)));
    }

    @Override
    public void setProcessing(LocalDate date, boolean processing) {
        processingFlags.put(getKey(date), processing);
        if (!processing) {
            progressMap.put(getKey(date), 100);
        }
    }

    @Override
    public int getProgress(LocalDate date) {
        return progressMap.getOrDefault(getKey(date), 0);
    }

    @Override
    public void setProgress(LocalDate date, int progress) {
        progressMap.put(getKey(date), Math.min(100, Math.max(0, progress)));
    }

    @Override
    public boolean isCacheComplete(LocalDate date) {
        return Boolean.TRUE.equals(completeFlags.get(getKey(date)));
    }

    @Override
    public void setCacheComplete(LocalDate date) {
        completeFlags.put(getKey(date), true);
        processingFlags.put(getKey(date), false);
        progressMap.put(getKey(date), 100);
    }

    @Override
    public void mergeHeatData(LocalDate date, List<HeatData> newData) {
        String key = getKey(date);
        List<HeatData> existingData = heatDataCache.computeIfAbsent(key, k -> new ArrayList<>());
        
        // 使用坐标作为key去重
        Map<String, HeatData> existingMap = new ConcurrentHashMap<>();
        for (HeatData hd : existingData) {
            String coordKey = String.format("%.3f,%.3f", hd.getLng(), hd.getLat());
            existingMap.put(coordKey, hd);
        }
        
        int addedCount = 0;
        for (HeatData newHd : newData) {
            String coordKey = String.format("%.3f,%.3f", newHd.getLng(), newHd.getLat());
            if (!existingMap.containsKey(coordKey)) {
                existingData.add(newHd);
                existingMap.put(coordKey, newHd);
                addedCount++;
            } else {
                // 如果已存在，更新count
                HeatData existing = existingMap.get(coordKey);
                existing.setCount(existing.getCount() + newHd.getCount());
            }
        }
        
        if (addedCount > 0) {
            heatDataCache.put(key, new ArrayList<>(existingData));
        }
    }
}