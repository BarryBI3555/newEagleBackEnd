package com.example.demo.entity;

import lombok.Data;

/**
 * 保单年赔付率-支公司-客户群 实体
 * 对应表: acd_pflbdn_khq_zgs
 */
@Data
public class AcdPflbdnKhqZgs {

    private Long id;

    private String tjDate;

    private String comnameSgs;

    private String comname;

    private String khq;

    private Double sumpaidYh;
    private Double sumpaidWh;
    private Double sumpaidHj;
    private Double yzbf19;
    private String sgndPfl;
    private String pflTb;
    private Double yjAjl;
    private Double wjAjl;
    private Double ajl;
    private Double yzbd;
    private String clv;
    private String clvTb;
    private Double yhaj;
    private Double whaj;
    private Double bgaj;
    private String bgajTb;
    private Double djyz;
    private String djyzTb;
    private Double yjCs;
    private Double yjRs;
    private Double yjWs;
    private Double csAjl;
    private Double rsAjl;
    private Double wsAjl;
    private Double csYjaj;
    private Double rsYjaj;
    private Double wsYjaj;

    private String maxTjTime;
}