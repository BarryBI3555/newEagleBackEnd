package com.example.demo.entity;

import lombok.Data;

/**
 * 车险结案率-人员 实体
 * 对应表: acd_pacll_ry
 */
@Data
public class AcdPacllRy {

    private Long id;

    private Integer idd;

    private String tjDate;          // 统计日期

    private String bm;              // 部门

    private String username;        // 人员名称

    private String usercode;        // 人员编码

    private Integer xzajl;          // 险种案件量

    private Integer yjajl;          // 已结案件量

    private Integer wjajl;          // 未结案件量

    private String pacll;           // 结案率

    private Integer wjclxs;         // 未结处理系数

    private Integer bsrswjcl;       // 本人日人均未结处理

    private Integer bsrswjclxs;     // 本人日人均未结处理系数

    private String grid;            // 网格

    private String groups;          // 小组

    private Double lajal;           // 立案案均率

    private String maxTjTime;       // 最大统计时间（用于前端标题展示）
}