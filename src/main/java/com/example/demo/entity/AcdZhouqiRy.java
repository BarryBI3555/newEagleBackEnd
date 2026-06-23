package com.example.demo.entity;

import lombok.Data;

/**
 * 周期-人员
 * 对应表: acd_zhouqi_ry
 */
@Data
public class AcdZhouqiRy {

    private String tjdate;

    private String comnameSgs;
    private String comcodeCk;
    private String comnameCk;
    private String groups;
    private String username;
    private String usercode;

    private Double zhouqiZt;       // 整体结案周期（天）
    private Double zhouqiWyn;      // 万元内案件结案周期（天）
    private Double zhouqiWys;      // 万元以上案件结案周期（天）
    private Double chakanZt;       // 查勘周期（天）
    private Double cuidingZt;      // 催定周期（天）
    private Double dingsunZt;      // 定损周期（天）
    private Double zhifuZt;        // 定损完成-支付（天）

    private String maxTjTime;
}
