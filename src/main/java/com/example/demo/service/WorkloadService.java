package com.example.demo.service;

import com.example.demo.entity.WorkloadDeptData;
import com.example.demo.entity.WorkloadGroupData;
import com.example.demo.entity.WorkloadEmpData;

import java.util.List;

/**
 * 工作量查询服务接口
 * 支持部门/小组/员工三级钻取，按月/周/日粒度聚合
 */
public interface WorkloadService {

    /** 获取最大统计日期（通用） */
    String getMaxTjDate(String tableName);

    /** 部门级工作量趋势 */
    List<WorkloadDeptData> getDepartmentWorkload(String startDate, String endDate, String comName, String granularity);

    /** 小组级工作量趋势 */
    List<WorkloadGroupData> getGroupWorkload(String comCode, String startDate, String endDate, String groups, String granularity);

    /** 员工级工作量趋势（含异常标识） */
    List<WorkloadEmpData> getEmployeeWorkload(String groupsCode, String startDate, String endDate, String userName, String granularity);

    /** 根据部门名称获取编码 */
    String getComCodeByName(String comName);

    /** 根据部门编码 + 小组名称获取小组编码 */
    String getGroupsCodeByComAndName(String comCode, String groupsName);
}