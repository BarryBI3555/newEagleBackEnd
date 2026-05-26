package com.example.demo.entity;

import lombok.Data;

@Data
public class CurGzlTableRs {

    private Long id;

    private String comCode;   // 部门编码

    private String comName;   // 部门名称

    private Integer shouGen;        // 首跟

    private Integer houGen;         // 后跟

    private Integer tiaojieZhuyuan; // 调解-住院

    private Integer tiaojieMenzheng; // 调解-门诊

    private Integer jieanZhuyuan;  // 结案-住院

    private Integer jieanMengzheng; // 结案-门诊

    private String tjDate;    // 统计日期

    private String maxTjTime; // 统计时间
}