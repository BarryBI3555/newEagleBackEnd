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
    private static final String SUCCESS_CODE = "200";

    @Autowired
    private AcdJlySiteMapper acdJlySiteMapper;

    @Value("${querybo.url}")
    private String QUERYBOURL;

    @Value("${querybo.insert-enabled:true}")
    private boolean insertEnabled;

    @Value("${locationsync.enabled:true}")
    private boolean locationSyncEnabled;

    private static final SimpleDateFormat HOUR_FORMATTER =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 定时获取数据 — 逻辑与 settlementClaim 完全一致
     */
    @Scheduled(cron = "${querybo.cron:0 0/10 * * * ?}")
    public void getQueryBo() {
        if (!locationSyncEnabled) {
            logger.info("定位同步已关闭，跳过本次执行");
            return;
        }
        Date date = new Date();
        String startTime = HOUR_FORMATTER.format(addHour(date, -1));
        String endTime = HOUR_FORMATTER.format(date);

        List<NameValuePair> params = new ArrayList<>(3);
        params.add(new BasicNameValuePair("startTime", startTime));
        params.add(new BasicNameValuePair("endTime", endTime));
        params.add(new BasicNameValuePair("account", ""));

        logger.info("查询轨迹参数：{}", JSON.toJSONString(params));

        try {
            String result = PiccHttpUtils.postWithBodyByX(QUERYBOURL, params);
            logger.info("查询轨迹返回：{}", result);

            if (result == null || result.isEmpty()) {
                logger.warn("查询轨迹返回为空，跳过本次同步");
                return;
            }

            JSONObject resultObj = JSON.parseObject(result);
            String code = resultObj.getString("code");
            logger.info("解析返回 code：{}", code);

            if (!SUCCESS_CODE.equals(code)) {
                logger.warn("查询轨迹返回 code 非0，code={}，msg={}", code, resultObj.getString("msg"));
                return;
            }

            JSONObject dataObj = resultObj.getJSONObject("data");
            if (dataObj == null) {
                logger.warn("查询轨迹返回 data 为空：{}", result);
                return;
            }

            JSONArray jsonArray = dataObj.getJSONArray("listBean");
            if (jsonArray == null || jsonArray.isEmpty()) {
                logger.warn("查询轨迹返回 listBean 为空，当前时间窗口无数据");
                return;
            }

            logger.info("解析到 {} 条轨迹记录，开始处理", jsonArray.size());
            processLocationData(jsonArray);
        } catch (Exception e) {
            logger.error("获取经纬度数据失败", e);
        }
    }

    private void processLocationData(JSONArray jsonArray) {
        List<AcdJlySite> insertList = new ArrayList<>();
        Integer maxId = acdJlySiteMapper.getMaxId();
        int nextId = (maxId == null ? 0 : maxId) + 1;
        logger.info("当前 maxId={}，下一条Id从 {} 开始", maxId, nextId);

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject info = jsonArray.getJSONObject(i);
            String usercode = info.getString("account");
            logger.info("处理第 {}/{} 条，usercode={}", i + 1, jsonArray.size(), usercode);

            JSONArray locationList = info.getJSONArray("locationList");
            if (locationList == null || locationList.isEmpty()) {
                logger.info("  usercode={} 无 locationList，跳过", usercode);
                continue;
            }

            logger.info("  usercode={} 有 {} 个轨迹点", usercode, locationList.size());
            for (int j = 0; j < locationList.size(); j++) {
                JSONObject locationInfo = locationList.getJSONObject(j);
                Date reportTime = locationInfo.getDate("reportTime");
                String reportTimeStr = HOUR_FORMATTER.format(reportTime);

                int count = acdJlySiteMapper.countByUsercodeAndReporttime(usercode, reportTimeStr);
                logger.info("轨迹点 {}/{}：usercode={}, reportTime={}, 已存在记录数={}",
                    j + 1, locationList.size(), usercode, reportTimeStr, count);

                if (count <= 0) {
                    AcdJlySite site = new AcdJlySite();
                    site.setId(nextId++);
                    site.setUsercode(usercode);
                    site.setReporttime(reportTime);
                    site.setLongitude(locationInfo.getBigDecimal("longitude"));
                    site.setLatitude(locationInfo.getBigDecimal("latitude"));
                    insertList.add(site);
                    logger.info("    -> 待插入，nextId={}", nextId - 1);
                } else {
                    logger.info("    -> 跳过（已存在）");
                }
            }
        }

        logger.info("共筛选出 {} 条待插入记录，insertEnabled={}", insertList.size(), insertEnabled);
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
        } else {
            logger.warn("没有需要插入的记录");
        }
    }

    private Date addHour(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        return cal.getTime();
    }
}
