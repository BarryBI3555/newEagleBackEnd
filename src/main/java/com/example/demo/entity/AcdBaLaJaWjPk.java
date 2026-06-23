package com.example.demo.entity;

import lombok.Data;

/**
 * 车险案件量-承保地（按量赔款）
 * 对应表: acd_ba_la_ja_wj_pk
 */
@Data
public class AcdBaLaJaWjPk {

    private String tjdate;

    private String comnameSgs;

    private Long bal;              // 报案量
    private Long balTb;            // 报案量-同比
    private Long balRs;            // 人伤报案量
    private Long balRsTb;          // 人伤报案量-同比
    private String rsbaZb;         // 人伤案件占比
    private Long rsbaZbTb;         // 人伤案件占比-同比
    private Long lal;              // 立案量
    private Long lalTb;            // 立案量-同比
    private Long jal;              // 结案量
    private Long jalTb;            // 结案量-同比
    private Long wjl;              // 未决存量
    private Long wjlTb;            // 未决存量-同比
    private Double sumestipaid;    // 未决估计赔款（亿）
    private Long sumestipaidTb;    // 估计赔款-同比
    private Double sumpaid;        // 整体结案金额（亿）
    private Long sumpaidTb;        // 整体结案金额-同比
    private Double sumpaidCs;      // 车损结案金额（亿）
    private Long sumpaidCsTb;      // 车损结案金额-同比
    private Double sumpaidRs;      // 人伤结案金额（亿）
    private Long sumpaidRsTb;      // 人伤结案金额-同比
    private String rspkZb;         // 人伤赔款占比
    private Long rspkZbTb;         // 人伤赔款占比-同比

    private String maxTjTime;
}
