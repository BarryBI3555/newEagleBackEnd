package com.example.demo.service;

import com.example.demo.entity.CurGzlTableRy;
import com.example.demo.entity.CurGzlTableBm;
import com.example.demo.entity.CurGzlTableRs;
import com.example.demo.entity.CurGzlTableGroup;

import java.util.List;

public interface ReportTableService {
    // 通用获取最大日期
    String getMaxTjDate(String tableName);

    List<CurGzlTableRy> getCurGzlData(String startDate, String endDate, String comName, String groups, String userName);

    // 新增：按部门统计
    List<CurGzlTableBm> getCurGzlDataBm(String startDate, String endDate, String comName);

    // 小组统计表
    List<CurGzlTableGroup> getCurGzlDataGroup(
            String startDate,
            String endDate,
            String comName,
            String groups
    );

    // 人员/住院门诊统计表（新表）
    List<CurGzlTableRs> getCurGzlDataRs(String startDate, String endDate, String comName);
}