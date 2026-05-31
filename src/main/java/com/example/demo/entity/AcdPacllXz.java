package com.example.demo.entity;

import lombok.Data;

/**
 * 车险结案率-小组 实体
 * 对应表: acd_pacll_xz
 */
@Data
public class AcdPacllXz {

    private Long id;

    private String comname;          // 部门

    private String tjDate;          // 统计日期

    private String groups;          // 小组

    private String groupscode;      // 小组编码

    private Integer xzl;            // 险种量

    private Integer yjl;            // 已结量

    private Integer wjl;            // 未结量

    private Integer wjlFr;          // 未结量(分人)

    private Integer wjlRs;          // 未结量(人数)

    private Double pacll;           // 结案率

    private Double lajal;           // 立案案均率

    private Double rsPp;            // 人数匹配

    private String maxTjTime;       // 最大统计时间（用于前端标题展示）
}