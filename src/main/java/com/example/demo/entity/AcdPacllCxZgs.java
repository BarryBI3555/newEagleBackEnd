package com.example.demo.entity;

import lombok.Data;

/**
 * 车险结案率-支公司
 * 对应表: acd_pacll_cx_zgs
 */
@Data
public class AcdPacllCxZgs {

    private String tjdate;

    private String comnameSgs;
    private String comname;

    private Long xzlBn;            // 新增案件量
    private Long yclBn;            // 已结案件量
    private Long qnWjl;            // 去年末未决
    private Long dqwj;             // 当前未决
    private Long dqwjQn;           // 去年同期未决
    private Long cll;              // 结案率
    private Long lajal;            // 立案结案率
    private Long cllTb;            // 结案率同比
    private Long lajalTb;          // 立案结案率同比

    private String maxTjTime;
}
