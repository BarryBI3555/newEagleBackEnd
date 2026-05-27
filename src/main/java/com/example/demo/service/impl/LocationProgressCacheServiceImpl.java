package com.example.demo.service.impl;

import com.example.demo.service.LocationProgressCacheService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 人员位置数据地址解析进度追踪服务实现
 */
@Service
public class LocationProgressCacheServiceImpl implements LocationProgressCacheService {

    private final Map<String, Boolean> processingFlags = new ConcurrentHashMap<>();
    private final Map<String, Integer> progressMap = new ConcurrentHashMap<>();
    private final Map<String, Boolean> completeFlags = new ConcurrentHashMap<>();

    private String getKey(LocalDate date) {
        return date.toString();
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
    public boolean isComplete(LocalDate date) {
        return Boolean.TRUE.equals(completeFlags.get(getKey(date)));
    }

    @Override
    public void setComplete(LocalDate date) {
        String key = getKey(date);
        completeFlags.put(key, true);
        processingFlags.put(key, false);
        progressMap.put(key, 100);
    }
}
