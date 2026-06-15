package com.example.demo.entity;

import lombok.Data;
import java.util.List;

/**
 * 小组级工作量趋势数据
 */
@Data
public class WorkloadGroupData {
    private String groupsCode;
    private String groupsName;
    private String comCode;
    private List<MonthData> data;

    @Data
    public static class MonthData {
        private String period;
        private Integer zl;
        private Integer ja;
        private Integer ckJsl;
        private Integer dsTjl;
        private Boolean isAbnormal;   // 是否异常
        private String abnormalType;   // 异常类型：spike / trend
    }
}