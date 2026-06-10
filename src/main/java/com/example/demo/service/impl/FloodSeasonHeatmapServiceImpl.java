package com.example.demo.service.impl;

import com.example.demo.entity.AcdOldCaseRainXq;
import com.example.demo.entity.HeatData;
import com.example.demo.mapper.AcdOldCaseRainXqMapper;
import com.example.demo.service.FloodSeasonHeatmapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FloodSeasonHeatmapServiceImpl implements FloodSeasonHeatmapService {

    private static final Logger logger = LoggerFactory.getLogger(FloodSeasonHeatmapServiceImpl.class);

    private static final int MAX_HEAT_POINTS = 10000;

    private static final double CD_MIN_LNG = 102.5;
    private static final double CD_MAX_LNG = 104.9;
    private static final double CD_MIN_LAT = 30.0;
    private static final double CD_MAX_LAT = 31.5;

    @Autowired
    private AcdOldCaseRainXqMapper acdOldCaseRainXqMapper;

    @Override
    public List<HeatData> getHeatData(String date) {
        logger.info("获取汛期热力图数据: date={}", date);
        List<AcdOldCaseRainXq> records;
        if (date == null || date.isEmpty()) {
            records = acdOldCaseRainXqMapper.selectWithCoordinates();
        } else {
            records = acdOldCaseRainXqMapper.selectByDate(date);
        }
        return buildHeatDataList(records);
    }

    @Override
    public List<HeatData> getAllHeatData() {
        logger.info("获取全部汛期热力图数据");
        List<AcdOldCaseRainXq> records = acdOldCaseRainXqMapper.selectWithCoordinates();
        return buildHeatDataList(records);
    }

    private List<HeatData> buildHeatDataList(List<AcdOldCaseRainXq> records) {
        List<HeatData> result = new ArrayList<>();
        if (records == null || records.isEmpty()) {
            return result;
        }

        Map<String, HeatData> groupMap = new HashMap<>();
        for (AcdOldCaseRainXq record : records) {
            BigDecimal latBg = record.getLatitude();
            BigDecimal lngBg = record.getLongitude();
            if (latBg == null || lngBg == null) {
                continue;
            }
            double lat = latBg.setScale(5, RoundingMode.HALF_UP).doubleValue();
            double lng = lngBg.setScale(5, RoundingMode.HALF_UP).doubleValue();

            if (lng < CD_MIN_LNG || lng > CD_MAX_LNG || lat < CD_MIN_LAT || lat > CD_MAX_LAT) {
                continue;
            }

            String key = lat + "," + lng;
            HeatData hd = groupMap.get(key);
            if (hd == null) {
                hd = new HeatData();
                hd.setLat(lat);
                hd.setLng(lng);
                hd.setCount(1);
                groupMap.put(key, hd);
            } else {
                hd.setCount(hd.getCount() + 1);
            }
        }

        result.addAll(groupMap.values());
        result.sort((a, b) -> Integer.compare(b.getCount(), a.getCount()));

        if (result.size() > MAX_HEAT_POINTS) {
            logger.warn("热力图数据量超过最大限制 {}，已截断", MAX_HEAT_POINTS);
            return result.subList(0, MAX_HEAT_POINTS);
        }

        logger.info("热力图数据构建完成，共 {} 个坐标点", result.size());
        return result;
    }
}