package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.AcdJlySite;
import com.example.demo.mapper.AcdJlySiteMapper;
import com.example.demo.util.PiccHttpUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class LocationSyncService {

    private static final Logger logger = LoggerFactory.getLogger(LocationSyncService.class);
    private static final String SUCCESS_CODE = "0";

    @Autowired
    private AcdJlySiteMapper acdJlySiteMapper;

    @Value("${querybo.url}")
    private String QUERYBOURL;

    @Value("${querybo.insert-enabled:true}")
    private boolean insertEnabled;

    private static final SimpleDateFormat HOUR_FORMATTER =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 定时获取数据 — 逻辑与 settlementClaim 完全一致
     */
    @Scheduled(cron = "${querybo.cron:0 0/10 * * * ?}")
    public void getQueryBo() {
        Date date = new Date();
        String startTime = HOUR_FORMATTER.format(addHour(date, -1));
        String endTime = HOUR_FORMATTER.format(date);

        List<NameValuePair> params = new ArrayList<>(3);
        params.add(new BasicNameValuePair("startTime", startTime));
        params.add(new BasicNameValuePair("endTime", endTime));
        params.add(new BasicNameValuePair("account", ""));

        logger.info("查询轨迹参数：" + JSON.toJSONString(params));

        try {
            String result = PiccHttpUtils.postWithBodyByX(QUERYBOURL, params);
            logger.info("查询轨迹返回：" + result);

            if (result != null && !result.isEmpty() && SUCCESS_CODE.equals(JSON.parseObject(result).getString("code"))) {
                JSONArray jsonArray = JSON.parseObject(result)
                    .getJSONObject("data")
                    .getJSONArray("listBean");

                if (jsonArray != null && jsonArray.size() > 0) {
                    processLocationData(jsonArray);
                }
            }
        } catch (Exception e) {
            logger.error("获取经纬度数据失败", e);
        }
    }

    private void processLocationData(JSONArray jsonArray) {
        List<AcdJlySite> insertList = new ArrayList<>();
        Integer maxId = acdJlySiteMapper.getMaxId();
        int nextId = (maxId == null ? 0 : maxId) + 1;

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject info = jsonArray.getJSONObject(i);
            String usercode = info.getString("account");
            JSONArray locationList = info.getJSONArray("locationList");

            if (locationList != null && locationList.size() > 0) {
                for (int j = 0; j < locationList.size(); j++) {
                    JSONObject locationInfo = locationList.getJSONObject(j);
                    String reportTimeStr = HOUR_FORMATTER.format(locationInfo.getDate("reportTime"));

                    int count = acdJlySiteMapper.countByUsercodeAndReporttime(usercode, reportTimeStr);

                    if (count <= 0) {
                        AcdJlySite site = new AcdJlySite();
                        site.setId(nextId++);
                        site.setUsercode(usercode);
                        site.setReporttime(locationInfo.getDate("reportTime"));
                        site.setLongitude(locationInfo.getBigDecimal("longitude"));
                        site.setLatitude(locationInfo.getBigDecimal("latitude"));
                        insertList.add(site);
                    }
                }
            }
        }

        if (!insertList.isEmpty()) {
            if (insertEnabled) {
                acdJlySiteMapper.batchInsert(insertList);
                logger.info("批量插入经纬度数据：{} 条", insertList.size());
            } else {
                logger.info("【调试模式】跳过插入，共 {} 条数据待插入", insertList.size());
                for (AcdJlySite site : insertList) {
                    logger.info("【调试数据】usercode={}, longitude={}, latitude={}, reporttime={}",
                        site.getUsercode(), site.getLongitude(), site.getLatitude(), site.getReporttime());
                }
            }
        }
    }

    private Date addHour(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        return cal.getTime();
    }
}
