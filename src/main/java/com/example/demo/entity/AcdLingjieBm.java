package com.example.demo.entity;

import lombok.Data;

/**
 * 零结案-部门
 * 对应表: acd_lingjie_bm
 */
@Data
public class AcdLingjieBm {

    private String tjdate;

    private String comcode;
    private String comname;

    private Long yjl1_3;
    private Long yjl4;
    private Long yjlWeek;
    private Long ljl1_3;
    private Long ljl4;
    private Long ljlWeek;
    private String lj1_3Pp;
    private String lj4Pp;
    private String ljWeekPp;

    private String maxTjTime;
}
