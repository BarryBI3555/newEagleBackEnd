package com.example.demo.service.impl;

import com.example.demo.config.HeatmapProperties;
import com.example.demo.entity.GeocodeResult;
import com.example.demo.entity.HeatData;
import com.example.demo.entity.LocationCache;
import com.example.demo.entity.PrplCheckTask;
import com.example.demo.mapper.LocationCacheMapper;
import com.example.demo.mapper.PrplCheckTaskMapper;
import com.example.demo.service.AsyncGeocodeService;
import com.example.demo.service.HeatDataCacheService;
import com.example.demo.util.LocationAddressConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AsyncGeocodeServiceImpl implements AsyncGeocodeService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncGeocodeServiceImpl.class);
    private static final DateTimeFormatter CACHE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private PrplCheckTaskMapper prplCheckTaskMapper;

    @Autowired
    private LocationCacheMapper locationCacheMapper;

    @Autowired
    private LocationAddressConverter addressConverter;

    @Autowired
    private HeatDataCacheService cacheService;

    @Autowired
    private HeatmapProperties heatmapProperties;

    @Override
    public void doGeocodeAddresses(LocalDate date, String dateStr) {
        logger.info("Start async geocode task: date={}", dateStr);

        try {
            List<PrplCheckTask> tasks = prplCheckTaskMapper.getMissingCoordinateTasksByDate(dateStr);
            Map<String, Integer> addressCounts = buildAddressCounts(tasks);

            if (addressCounts.isEmpty()) {
                logger.info("No addresses need geocoding: date={}", dateStr);
                cacheService.setStats(date, 0, 0, 0, 0);
                cacheService.setCacheComplete(date);
                return;
            }

            int total = addressCounts.values().stream().mapToInt(Integer::intValue).sum();
            int processed = 0;
            int successCount = 0;
            int failCount = 0;
            cacheService.setStats(date, total, processed, successCount, failCount);

            List<String> addresses = new ArrayList<>(addressCounts.keySet());
            Map<String, LocationCache> validCacheByAddress = findValidCacheByAddress(addresses);
            List<HeatData> pendingHeatData = new ArrayList<>();

            logger.info("Async geocode workload: rows={}, uniqueAddresses={}, cachedAddresses={}",
                    total, addresses.size(), validCacheByAddress.size());

            for (String address : addresses) {
                int rowCount = addressCounts.get(address);

                if (Thread.currentThread().isInterrupted()) {
                    int progress = calculateProgress(processed, total);
                    logger.info("Async geocode task cancelled: date={}, processed={}/{}", dateStr, processed, total);
                    cacheService.setProcessing(date, false);
                    cacheService.setProgress(date, progress);
                    return;
                }

                boolean success;
                LocationCache cached = validCacheByAddress.get(address);
                if (cached != null) {
                    success = appendHeatData(pendingHeatData, cached.getLongitude(), cached.getLatitude(), rowCount);
                } else {
                    success = geocodeAddress(address, rowCount, pendingHeatData);
                }

                processed += rowCount;
                if (success) {
                    successCount += rowCount;
                } else {
                    failCount += rowCount;
                }

                cacheService.setStats(date, total, processed, successCount, failCount);
                cacheService.setProgress(date, calculateProgress(processed, total));

                if (pendingHeatData.size() >= 50) {
                    cacheService.mergeHeatData(date, pendingHeatData);
                    pendingHeatData.clear();
                }
            }

            if (!pendingHeatData.isEmpty()) {
                cacheService.mergeHeatData(date, pendingHeatData);
            }

            logger.info("Async geocode completed: date={}, total={}, success={}, failed={}",
                    dateStr, total, successCount, failCount);
            cacheService.setProgress(date, 100);
            cacheService.setCacheComplete(date);
        } catch (Exception e) {
            logger.error("Async geocode task failed: date={}, {}", dateStr, e.getMessage(), e);
            cacheService.setProcessing(date, false);
        }
    }

    private Map<String, Integer> buildAddressCounts(List<PrplCheckTask> tasks) {
        Map<String, Integer> addressCounts = new LinkedHashMap<>();
        if (tasks == null || tasks.isEmpty()) {
            return addressCounts;
        }

        for (PrplCheckTask task : tasks) {
            if (task == null || task.getChecksite() == null) {
                continue;
            }
            String address = task.getChecksite().trim();
            if (address.isEmpty()) {
                continue;
            }
            addressCounts.put(address, addressCounts.getOrDefault(address, 0) + 1);
        }
        return addressCounts;
    }

    private Map<String, LocationCache> findValidCacheByAddress(List<String> addresses) {
        Map<String, LocationCache> result = new LinkedHashMap<>();
        if (addresses == null || addresses.isEmpty()) {
            return result;
        }

        String now = CACHE_TIME_FORMATTER.format(LocalDateTime.now());
        int batchSize = 500;
        for (int start = 0; start < addresses.size(); start += batchSize) {
            int end = Math.min(start + batchSize, addresses.size());
            List<LocationCache> cachedItems = locationCacheMapper.findValidByAddressBatch(
                    addresses.subList(start, end), now);
            if (cachedItems == null || cachedItems.isEmpty()) {
                continue;
            }

            for (LocationCache item : cachedItems) {
                if (item != null && item.getAddress() != null) {
                    result.putIfAbsent(item.getAddress(), item);
                }
            }
        }
        return result;
    }

    private boolean geocodeAddress(String address, int rowCount, List<HeatData> pendingHeatData) {
        try {
            GeocodeResult result = addressConverter.geocode(address);
            if (result == null || result.getStatus() != 0 || result.getResult() == null
                    || result.getResult().getLocation() == null) {
                return false;
            }

            Double lng = result.getResult().getLocation().getLng();
            Double lat = result.getResult().getLocation().getLat();
            return appendHeatData(pendingHeatData, lng, lat, rowCount);
        } catch (Exception e) {
            logger.debug("Address geocode failed: address={}, {}", address, e.getMessage());
            return false;
        }
    }

    private boolean appendHeatData(List<HeatData> heatDataList, Double lng, Double lat, int count) {
        if (!heatmapProperties.isInRegion(lng, lat)) {
            return false;
        }

        HeatData heatData = new HeatData();
        heatData.setLng(lng);
        heatData.setLat(lat);
        heatData.setCount(count);
        heatDataList.add(heatData);
        return true;
    }

    private int calculateProgress(int processed, int total) {
        if (total <= 0) {
            return 100;
        }
        return Math.min(100, Math.max(0, (int) ((processed * 100.0) / total)));
    }
}
