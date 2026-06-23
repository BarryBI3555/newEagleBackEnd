package com.example.demo.entity;

import lombok.Data;

/**
 * 零结案-人员
 * 对应表: acd_lingjie_ry
 */
@Data
public class AcdLingjieRy {

    private String tjdate;

    private String comname;
    private String groups;
    private String username;
    private String usercode;

    private Long ljl1_3;           // 1-3月零结量
    private String lj1_3Pp;        // 1-3月零结量占比
    private Long ljl4;             // 当月零结量
    private String lj4Pp;          // 当月零结量占比
    private Long ljlWeek;          // 本周零结量
    private String ljWeekPp;       // 本周零结案占比

    private String maxTjTime;
}
