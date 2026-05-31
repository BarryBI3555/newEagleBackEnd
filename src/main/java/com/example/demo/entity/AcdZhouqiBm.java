package com.example.demo.entity;

import lombok.Data;

/**
 * 周期-部门 实体
 * 对应表: acd_zhouqi_bm
 */
@Data
public class AcdZhouqiBm {

    private Long id;

    private String tjDate;          // 统计日期

    private String comcode;          // 部门编码

    private String comname;          // 部门名称

    private Double zhouqiZt;        // 周期整体

    private Double zhouqiWyn;       // 周期万元内

    private Double zhouqiWys;       // 周期万元以上

    private Double chakanZt;        // 查勘整体

    private Double cuidingZt;       // 催定整体

    private Double dingsunZt;       // 定损整体

    private Double zhifuZt;         // 支付整体

    private String maxTjTime;       // 最大统计时间（用于前端标题展示）
}