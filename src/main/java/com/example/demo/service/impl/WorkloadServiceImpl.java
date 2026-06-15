package com.example.demo.service.impl;

import com.example.demo.entity.CurGzlTableBm;
import com.example.demo.entity.CurGzlTableGroup;
import com.example.demo.entity.CurGzlTableRy;
import com.example.demo.entity.WorkloadDeptData;
import com.example.demo.entity.WorkloadGroupData;
import com.example.demo.entity.WorkloadEmpData;
import com.example.demo.mapper.WorkloadMapper;
import com.example.demo.service.WorkloadService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkloadServiceImpl implements WorkloadService {

    @Resource
    private WorkloadMapper workloadMapper;

    @Override
    public String getMaxTjDate(String tableName) {
        return workloadMapper.getMaxTjDateByTable(tableName);
    }

    @Override
    public List<WorkloadDeptData> getDepartmentWorkload(
            String startDate, String endDate, String comName, String granularity) {

        // 查询日粒度原始数据
        List<CurGzlTableBm> rawData = workloadMapper.getWorkloadBmData(startDate, endDate, comName);

        // 按部门分组，再按时间粒度聚合
        Map<String, List<CurGzlTableBm>> byDept = rawData.stream()
                .collect(Collectors.groupingBy(CurGzlTableBm::getComName, LinkedHashMap::new, Collectors.toList()));

        List<WorkloadDeptData> result = new ArrayList<>();
        for (Map.Entry<String, List<CurGzlTableBm>> entry : byDept.entrySet()) {
            WorkloadDeptData dept = new WorkloadDeptData();
            dept.setComCode(entry.getValue().get(0).getComCode());
            dept.setComName(entry.getKey());
            dept.setData(aggregateBmData(entry.getValue(), granularity));
            result.add(dept);
        }
        return result;
    }

    @Override
    public List<WorkloadGroupData> getGroupWorkload(
            String comCode, String startDate, String endDate, String groups, String granularity) {

        List<CurGzlTableGroup> rawData = workloadMapper.getWorkloadGroupData(startDate, endDate, comCode, groups);

        // 过滤 groupsCode 为空的脏数据
        List<CurGzlTableGroup> validData = rawData.stream()
                .filter(g -> g.getGroupsCode() != null && !g.getGroupsCode().trim().isEmpty())
                .collect(Collectors.toList());

        // 按小组分组
        Map<String, List<CurGzlTableGroup>> byGroup = validData.stream()
                .collect(Collectors.groupingBy(CurGzlTableGroup::getGroups, LinkedHashMap::new, Collectors.toList()));

        List<WorkloadGroupData> result = new ArrayList<>();
        for (Map.Entry<String, List<CurGzlTableGroup>> entry : byGroup.entrySet()) {
            WorkloadGroupData group = new WorkloadGroupData();
            List<CurGzlTableGroup> groupData = entry.getValue();
            group.setGroupsCode(groupData.get(0).getGroupsCode());
            group.setGroupsName(entry.getKey());
            group.setComCode(groupData.get(0).getComCode());
            group.setData(aggregateGroupData(groupData, granularity));
            result.add(group);
        }
        return result;
    }

    @Override
    public List<WorkloadEmpData> getEmployeeWorkload(
            String groupsCode, String startDate, String endDate, String userName, String granularity) {

        List<CurGzlTableRy> rawData = workloadMapper.getWorkloadRyData(startDate, endDate, groupsCode, userName);

        // 按员工分组
        Map<String, List<CurGzlTableRy>> byEmp = rawData.stream()
                .collect(Collectors.groupingBy(CurGzlTableRy::getUserCode, LinkedHashMap::new, Collectors.toList()));

        // 计算同组平均工作量用于异常判定
        double groupAvg = rawData.stream()
                .mapToInt(CurGzlTableRy::getZl)
                .average()
                .orElse(0);
        double groupStd = calculateStd(rawData.stream()
                .map(CurGzlTableRy::getZl)
                .collect(Collectors.toList()), groupAvg);

        List<WorkloadEmpData> result = new ArrayList<>();
        for (Map.Entry<String, List<CurGzlTableRy>> entry : byEmp.entrySet()) {
            WorkloadEmpData emp = new WorkloadEmpData();
            List<CurGzlTableRy> empData = entry.getValue();
            emp.setUserCode(entry.getKey());
            emp.setUserName(empData.get(0).getUserName());
            emp.setGroupsCode(empData.get(0).getGroupsCode());
            emp.setGroupsName(empData.get(0).getGroups());
            emp.setComCode(empData.get(0).getComCode());

            // 计算员工总体是否异常（平均工作量低于同组1.5个标准差）
            double empAvg = empData.stream()
                    .mapToInt(CurGzlTableRy::getZl)
                    .average()
                    .orElse(0);
            emp.setIsAbnormal(groupStd > 0 ? (empAvg < groupAvg - 1.5 * groupStd) : false);

            emp.setData(aggregateRyData(empData, granularity));
            result.add(emp);
        }
        return result;
    }

    @Override
    public String getComCodeByName(String comName) {
        return workloadMapper.getComCodeByName(comName);
    }

    @Override
    public String getGroupsCodeByComAndName(String comCode, String groupsName) {
        return workloadMapper.getGroupsCodeByComAndName(comCode, groupsName);
    }

    // ==================== 私有聚合方法 ====================

    private List<WorkloadDeptData.MonthData> aggregateBmData(
            List<CurGzlTableBm> data, String granularity) {

        // 按时间粒度分组聚合
        Map<String, List<CurGzlTableBm>> grouped = data.stream()
                .collect(Collectors.groupingBy(
                        item -> getPeriodKey(item.getTjDate(), granularity),
                        LinkedHashMap::new,
                        Collectors.toList()));

        return grouped.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(entry -> {
                    WorkloadDeptData.MonthData md = new WorkloadDeptData.MonthData();
                    md.setPeriod(entry.getKey());
                    List<CurGzlTableBm> items = entry.getValue();
                    md.setZl(items.stream().mapToInt(CurGzlTableBm::getZl).sum());
                    md.setJa(items.stream().mapToInt(CurGzlTableBm::getJa).sum());
                    md.setCkJsl(items.stream().mapToInt(CurGzlTableBm::getCkJsl).sum());
                    md.setDsTjl(items.stream().mapToInt(CurGzlTableBm::getDsTjl).sum());
                    return md;
                })
                .collect(Collectors.toList());
    }

    private List<WorkloadGroupData.MonthData> aggregateGroupData(
            List<CurGzlTableGroup> data, String granularity) {

        Map<String, List<CurGzlTableGroup>> grouped = data.stream()
                .collect(Collectors.groupingBy(
                        item -> getPeriodKey(item.getTjDate(), granularity),
                        LinkedHashMap::new,
                        Collectors.toList()));

        return grouped.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(entry -> {
                    WorkloadGroupData.MonthData md = new WorkloadGroupData.MonthData();
                    md.setPeriod(entry.getKey());
                    List<CurGzlTableGroup> items = entry.getValue();
                    md.setZl(items.stream().mapToInt(CurGzlTableGroup::getZl).sum());
                    md.setJa(items.stream().mapToInt(CurGzlTableGroup::getJa).sum());
                    md.setCkJsl(items.stream().mapToInt(CurGzlTableGroup::getCkJsl).sum());
                    md.setDsTjl(items.stream().mapToInt(CurGzlTableGroup::getDsTjl).sum());
                    return md;
                })
                .collect(Collectors.toList());
    }

    private List<WorkloadEmpData.MonthData> aggregateRyData(
            List<CurGzlTableRy> data, String granularity) {

        Map<String, List<CurGzlTableRy>> grouped = data.stream()
                .collect(Collectors.groupingBy(
                        item -> getPeriodKey(item.getTjDate(), granularity),
                        LinkedHashMap::new,
                        Collectors.toList()));

        // 计算该员工在此期间的平均日工作量
        double empAvg = data.stream().mapToInt(CurGzlTableRy::getZl).average().orElse(0);
        double empStd = calculateStd(data.stream().map(CurGzlTableRy::getZl).collect(Collectors.toList()), empAvg);

        return grouped.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(entry -> {
                    WorkloadEmpData.MonthData md = new WorkloadEmpData.MonthData();
                    md.setPeriod(entry.getKey());
                    List<CurGzlTableRy> items = entry.getValue();
                    md.setZl(items.stream().mapToInt(CurGzlTableRy::getZl).sum());
                    md.setJa(items.stream().mapToInt(CurGzlTableRy::getJa).sum());
                    md.setCkJsl(items.stream().mapToInt(CurGzlTableRy::getCkJsl).sum());
                    md.setDsTjl(items.stream().mapToInt(CurGzlTableRy::getDsTjl).sum());
                    // 月度异常：当日均工作量低于员工平均的1.5个标准差
                    md.setIsAbnormal(empStd > 0 ? (empAvg < empAvg - 1.5 * empStd) : false);
                    return md;
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据粒度获取时间区间标识
     * month: "2026-01"
     * week: "2026-W01"（该周周一日期）
     * day: "2026-01-15"
     */
    private String getPeriodKey(String tjDate, String granularity) {
        if (tjDate == null || tjDate.length() < 10) return tjDate;

        LocalDate date = LocalDate.parse(tjDate.substring(0, 10));
        DateTimeFormatter df;

        switch (granularity) {
            case "month":
                df = DateTimeFormatter.ofPattern("yyyy-MM");
                return date.format(df);
            case "week":
                // 计算该日期对应的周一日期
                LocalDate monday = date.with(DayOfWeek.MONDAY);
                return monday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            case "day":
            default:
                return tjDate.substring(0, 10);
        }
    }

    private double calculateStd(List<Integer> values, double mean) {
        if (values.size() < 2) return 0;
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .sum() / values.size();
        return Math.sqrt(variance);
    }
}