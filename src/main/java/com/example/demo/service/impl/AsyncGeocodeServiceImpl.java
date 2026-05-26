package com.example.demo.service.impl;

import com.example.demo.entity.GeocodeResult;
import com.example.demo.entity.HeatData;
import com.example.demo.entity.PrplCheckTask;
import com.example.demo.mapper.PrplCheckTaskMapper;
import com.example.demo.service.AsyncGeocodeService;
import com.example.demo.service.HeatDataCacheService;
import com.example.demo.util.LocationAddressConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AsyncGeocodeServiceImpl implements AsyncGeocodeService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncGeocodeServiceImpl.class);

    @Autowired
    private PrplCheckTaskMapper prplCheckTaskMapper;

    @Autowired
    private LocationAddressConverter addressConverter;

    @Autowired
    private HeatDataCacheService cacheService;

    @Async("geocodeExecutor")
    @Override
    public void asyncGeocodeAddresses(LocalDate date, String dateStr) {
        logger.info("开始异步地址解析任务: date={}", dateStr);

        try {
            List<PrplCheckTask> tasks = prplCheckTaskMapper.getAllTasksByDate(dateStr);

            if (tasks == null || tasks.isEmpty()) {
                logger.info("没有需要解析的地址数据");
                cacheService.setCacheComplete(date);
                return;
            }

            List<PrplCheckTask> tasksToGeocode = new ArrayList<>();
            for (PrplCheckTask task : tasks) {
                Double lng = task.getChecklongitude();
                Double lat = task.getChecklatitude();
                String checksite = task.getChecksite();

                if ((lng == null || lat == null || Double.isNaN(lng) || Double.isNaN(lat)
                        || lng == 0 || lat == 0) && checksite != null && !checksite.trim().isEmpty()) {
                    tasksToGeocode.add(task);
                }
            }

            logger.info("需要解析的地址数量: {}", tasksToGeocode.size());

            if (tasksToGeocode.isEmpty()) {
                cacheService.setCacheComplete(date);
                return;
            }

            int total = tasksToGeocode.size();
            int processed = 0;
            int successCount = 0;
            int failCount = 0;

            List<HeatData> geocodeResults = new ArrayList<>();

            for (PrplCheckTask task : tasksToGeocode) {
                try {
                    GeocodeResult result = addressConverter.geocode(task.getChecksite());
                    if (result != null && result.getStatus() == 0 && result.getResult() != null
                            && result.getResult().getLocation() != null) {

                        Double lng = result.getResult().getLocation().getLng();
                        Double lat = result.getResult().getLocation().getLat();

                        HeatData heatData = new HeatData();
                        heatData.setLng(lng);
                        heatData.setLat(lat);
                        heatData.setCount(1);
                        geocodeResults.add(heatData);
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    logger.debug("地址解析失败: {}, {}", task.getChecksite(), e.getMessage());
                    failCount++;
                }

                processed++;

                if (processed % 10 == 0 || processed == total) {
                    int progress = (int) ((processed * 100.0) / total);
                    cacheService.setProgress(date, progress);

                    if (geocodeResults.size() >= 50) {
                        cacheService.mergeHeatData(date, geocodeResults);
                        geocodeResults.clear();
                    }
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            if (!geocodeResults.isEmpty()) {
                cacheService.mergeHeatData(date, geocodeResults);
            }

            logger.info("异步地址解析完成: 总数={}, 成功={}, 失败={}", total, successCount, failCount);

            cacheService.setProgress(date, 100);
            cacheService.setCacheComplete(date);

        } catch (Exception e) {
            logger.error("异步地址解析任务失败: {}", e.getMessage(), e);
            cacheService.setProcessing(date, false);
        }
    }
}
