package com.example.demo.entity;

import lombok.Data;

/**
 * 查勘量-年度每月 实体
 * 对应表: acd_chakan_year
 */
@Data
public class AcdChakanYear {

    private String tjdate;

    private String comnameSgs;

    private String comcodeSgs;

    private String comcode;

    private String comname;

    private String username;

    private String gwname;

    private String usercode;

    private String tjYear;

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
