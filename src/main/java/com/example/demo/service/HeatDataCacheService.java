package com.example.demo.service;

import com.example.demo.entity.HeatData;

import java.time.LocalDate;
import java.util.List;

/**
 * 热力图数据缓存服务
 */
public interface HeatDataCacheService {

    /**
     * 获取缓存的热力图数据
     */
    List<HeatData> getCachedHeatData(LocalDate date);

    /**
     * 仅获取缓存中的数据条数（O(1)，不复制列表）。用于 /hotmap/progress 轮询等只关心 size 的场景。
     * @param date 日期
     * @return 缓存中该日期的坐标点数；无缓存返回 0
     */
    int getCachedCount(LocalDate date);

    /**
     * 缓存热力图数据
     */
    void cacheHeatData(LocalDate date, List<HeatData> data);

    /**
     * 清除指定日期的缓存
     */
    void clearCache(LocalDate date);

    /**
     * 检查是否正在解析中
     */
    boolean isProcessing(LocalDate date);

    /**
     * 设置解析状态
     */
    void setProcessing(LocalDate date, boolean processing);

    /**
     * 获取解析进度（0-100）
     */
    int getProgress(LocalDate date);

    /**
     * 设置解析进度
     */
    void setProgress(LocalDate date, int progress);

    /**
     * 检查缓存是否完整（包含地址解析结果）
     */
    boolean isCacheComplete(LocalDate date);

    /**
     * 设置缓存为完整状态
     */
    void setCacheComplete(LocalDate date);

    /**
     * 合并新解析的数据到缓存
     */
    void mergeHeatData(LocalDate date, List<HeatData> newData);
}