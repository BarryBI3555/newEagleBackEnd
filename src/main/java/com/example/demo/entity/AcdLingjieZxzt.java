package com.example.demo.entity;

import lombok.Data;

/**
 * 零结案-中心整体
 * 对应表: acd_lingjie_zxzt
 */
@Data
public class AcdLingjieZxzt {

    private String tjdate;

    private String comcode;
    private String comname;

    private Long lingjieNum1_3;
    private Long jieanNum1_3;
    private String lingjie1_3Pp;
    private Long lingjieNumMonth;
    private Long jieanNumMonth;
    private String lingjieMonthPp;
    private Long lingjieNumWeek;
    private Long jieanNumWeek;
    private String lingjieWeekPp;

    private String maxTjTime;
}
