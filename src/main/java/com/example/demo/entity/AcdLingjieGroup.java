package com.example.demo.entity;

import lombok.Data;

/**
 * 零结案-小组（合成结果）
 * 由 acd_lingjie_bm / acd_lingjie_groups / acd_lingjie_zxzt 三表在 Service 层内存合并得到。
 * bm 行: comname 有值, groups 为空
 * groups 行: comname + groups 均有值
 * zxzt 行: comname='整体', groups 为空
 */
@Data
public class AcdLingjieGroup {

    private String tjdate;

    private String comname;
    private String groups;

    private Long ljl1_3;
    private String lj1_3Pp;
    private Long ljl4;
    private String lj4Pp;
    private Long ljlWeek;
    private String ljWeekPp;

    private String maxTjTime;
}
