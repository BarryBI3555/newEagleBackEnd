package com.example.demo.entity;

import lombok.Data;

/**
 * 综合赔付率-险种 实体
 * 对应表: acd_zhpfl_xz
 */
@Data
public class AcdZhpflXz {

    private Long id;

    private String tjDate;

    private String comnameSgs;

    private String xl;

    private Double jbf;
    private Double pfcb;
    private Double fy;
    private Double glfy;
    private String zhcbl;
    private String zhcblTb;
    private String zhpl;
    private String zhplTb;

    private String maxTjTime;
}