package com.example.demo.entity;

import lombok.Data;

/**
 * 赔案处理率-部门实时
 * 对应表: acd_pacll_bm_shishi
 */
@Data
public class AcdPacllBmShishi {

    private Integer idd;
    private String comcode;
    private String comname;

    private Double pacll;          // 赔案处理率
    private Long xzl;              // 新增案件量
    private Long yjl;              // 已决案件量
    private Long wjl;              // 未决案件量
}
