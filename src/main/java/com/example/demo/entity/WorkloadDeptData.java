package com.example.demo.entity;

import lombok.Data;
import java.util.List;

/**
 * 部门级工作量趋势数据
 */
@Data
public class WorkloadDeptData {
    private String comCode;
    private String comName;
    private List<MonthData> data;

    @Data
    public static class MonthData {
        private String period;      // 月份/周/日，如 "2026-01"
        private Integer zl;        // 总量
        private Integer ja;        // 结案量
        private Integer ckJsl;     // 查勘接收量
        private Integer dsTjl;     // 定损提交量
        private Boolean isAbnormal;   // 是否异常
        private String abnormalType;   // 异常类型：spike / trend
    }
}