package com.example.demo.entity;

import lombok.Data;
import java.util.List;

/**
 * 员工级工作量趋势数据（含异常标识）
 */
@Data
public class WorkloadEmpData {
    private String userCode;
    private String userName;
    private String groupsCode;
    private String groupsName;
    private String comCode;
    private Boolean isAbnormal;        // 是否异常员工
    private List<MonthData> data;

    @Data
    public static class MonthData {
        private String period;
        private Integer zl;
        private Integer ja;
        private Integer ckJsl;
        private Integer dsTjl;
        private Boolean isAbnormal;   // 该月是否异常
    }
}