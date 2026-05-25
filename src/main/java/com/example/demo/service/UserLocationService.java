package com.example.demo.service;

import com.example.demo.entity.UserLocation;
import java.time.LocalDate;
import java.util.List;

public interface UserLocationService {
    // 获取所有位置
    List<UserLocation> getAllLocations();

    // 获取指定日期每个用户最新位置
    List<UserLocation> getLatestLocationsByDate(LocalDate date, String groupscode, String keyword);

    // 获取指定日期用户当天轨迹
    List<UserLocation> getUserLocationsByDate(String usercode, LocalDate date);
}