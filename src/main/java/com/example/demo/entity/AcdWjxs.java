package com.example.demo.entity;

import lombok.Data;

/**
 * 未决存量-案件类型
 * 对应表: acd_wjxs
 */
@Data
public class AcdWjxs {

    private String tjdate;     // 统计时间
    private String comname;    // 机构名称
    private String lflag;      // 类型

    private Long hj;           // 未决总量
    private Long bsrs;         // 不涉及人伤总量
    private Long rs;           // 涉及人伤总量

    private Double ztyj;       // 整体月均处理量
    private Double bsrsyj;     // 不涉及人伤月均处理量
    private Double rsyj;       // 涉及人伤月均处理量

    private Double ztwjxs;     // 整体未决存量系数
    private Double bsrswjxs;   // 不涉及人伤未决存量系数
    private Double rswjxs;     // 涉及人伤未决存量系数

    private String maxTjTime;  // 统计时间显示字段
}
