package com.example.demo.entity;

import lombok.Data;

/**
 * 车险结案率-部门 实体
 * 对应表: acd_pacll_bm
 */
@Data
public class AcdPacllBm {

    private Long idd;

    private String tjDate;          // 统计日期

    private Integer comcode;         // 机构代码

    private String comname;          // 机构名称

    private Integer xzl;            // 险种量

    private Integer yjl;            // 已结量

    private Integer wjl;            // 未结量

    private Integer wjlQn;          // 未结量(去年)

    private Double pacll;           // 结案率(%)

    private Double wjxs;            // 未决系数

    private Double rjwj;            // 日均未决

    private Integer wjlFr;          // 未结量(分人)

    private Double wjxsFr;          // 未决系数(分人)

    private Double rjwjFr;          // 日均未决(分人)

    private Double wjxsRs;          // 未决系数(人数)

    private Integer rswj;           // 人数未决

    private Double lajal;           // 立案案均率

    private Double rsZb;            // 人数占比

    private String maxTjTime;       // 最大统计时间（用于前端标题展示）
}