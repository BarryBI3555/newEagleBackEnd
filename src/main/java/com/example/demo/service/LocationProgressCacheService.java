package com.example.demo.service;

import java.time.LocalDate;

/**
 * 人员位置数据地址解析进度追踪服务
 */
public interface LocationProgressCacheService {

    boolean isProcessing(LocalDate date);

    void setProcessing(LocalDate date, boolean processing);

    int getProgress(LocalDate date);

    void setProgress(LocalDate date, int progress);

    boolean isComplete(LocalDate date);

    void setComplete(LocalDate date);
}
