package com.example.demo.controller;

import com.example.demo.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;



@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class AdministrativeRegionController {
    private static final Logger log = LoggerFactory.getLogger(AdministrativeRegionController.class);

    // ====================== 腾讯地图获取行政区划json ======================

    @Autowired
    private RestTemplate restTemplate;

    @Value("${tencent.map.key}")
    private String tencentMapKey;

    @Value("${tencent.map.domain}")
    private String mapDomain;

    @Value("${tencent.map.api-domain}")
    private String apiDomain;

    // 地理位置逆解析
    @GetMapping("/map/geocoder")
    public Result<Object> getGeocoder(@RequestParam String location) {
        try {
            String url = apiDomain + "ws/geocoder/v1/?location=" + location + "&key=" + tencentMapKey;
            return Result.success(restTemplate.getForObject(url, Object.class));
        } catch (Exception e) {
            log.error("请求失败", e);
            return Result.error("请求失败", e);
        }
    }

    //获取城市名称
    @GetMapping("/map/district/search")
    public Result<Object> getDistrictname(@RequestParam(required = false) String keyword) {
        try {
            // 参数校验
            if (keyword == null || keyword.trim().isEmpty() || keyword.equals("undefined")) {
                return Result.error("搜索关键词不能为空");
            }

            String url = apiDomain + "ws/district/v1/search?key=" + tencentMapKey + "&keyword=" + keyword + "&get_polygon=2&max_offset=100";
            return Result.success(restTemplate.getForObject(url, Object.class));
        } catch (Exception e) {
            log.error("请求失败", e);
            return Result.error("请求失败", e);
        }
    }

    // 获取下级行政区划
    @GetMapping("/map/district/getchildren")
    public Result<Object> getDistrictChildren(@RequestParam(required = false) String id) {
        try {
            // 参数校验
            if (id == null || id.trim().isEmpty() || id.equals("undefined")) {
                return Result.error("行政区划ID不能为空");
            }

            String url = apiDomain + "ws/district/v1/getchildren?key=" + tencentMapKey + "&id=" + id + "&get_polygon=2&max_offset=100";
            return Result.success(restTemplate.getForObject(url, Object.class));
        } catch (Exception e) {
            log.error("请求失败", e);
            return Result.error("请求失败", e);
        }
    }

}
