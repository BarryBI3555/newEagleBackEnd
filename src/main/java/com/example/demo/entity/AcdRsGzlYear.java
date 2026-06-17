package com.example.demo.entity;

import lombok.Data;

/**
 * 人伤跟踪量-年度每月 实体
 * 对应表: acd_rs_gzl_year
 */
@Data
public class AcdRsGzlYear {

    private String tjdate;

    private String comcode;

    private String comname;

    private String username;

    private String fenzu;

    private String usercode;

    private String tjYear;

    private String jaflag;

    private Long hj;

    private Long mon1;
    private Long mon2;
    private Long mon3;
    private Long mon4;
    private Long mon5;
    private Long mon6;
    private Long mon7;
    private Long mon8;
    private Long mon9;
    private Long mon10;
    private Long mon11;
    private Long mon12;

    private String maxTjTime;
}
