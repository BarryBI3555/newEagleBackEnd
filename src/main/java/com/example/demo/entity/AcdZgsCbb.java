package com.example.demo.entity;

import lombok.Data;

/**
 * 各支公司产保比 实体
 * 对应表: acd_zgs_cbb
 */
@Data
public class AcdZgsCbb {
    private Long id;
    private String tjDate;
    private String comnameSgs;
    private String comcodeSgs;
    private String comname;
    private String gscomcode;
    private String repairfactorytype;
    private Double sumverilossfee;
    private Double sumpremium;
    private String cbb;
    private String maxTjTime;
}
