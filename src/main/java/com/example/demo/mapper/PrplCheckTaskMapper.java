package com.example.demo.mapper;

import com.example.demo.entity.PrplCheckTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PrplCheckTaskMapper {

    int insert(PrplCheckTask prplCheckTask);

    PrplCheckTask selectById(Long id);

    int updateById(PrplCheckTask prplCheckTask);

    int deleteById(Long id);

    List<PrplCheckTask> getAllTasksByDate(String date);
    
    // 直接在数据库层面聚合统计坐标点（保留3位小数）
    List<Map<String, Object>> getHeatDataByDate(String date);
    
    // 统计指定日期的数据总量
    int countTasksByDate(String date);
}