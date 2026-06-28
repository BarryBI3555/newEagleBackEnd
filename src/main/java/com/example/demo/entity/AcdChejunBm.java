package com.example.demo.entity;

import lombok.Data;

/**
 * 车均定损-定损区域 实体
 * 对应表: acd_chejun_bm
 */
@Data
public class AcdChejunBm {

    private String tjdate;

    private String comcode;

    private String dsqy;

    private Double ajsBn;
    private Double ajsQn;
    private Double bncj;
    private Double qncj;
    private Double tb;
    private Double dsjeBn;
    private Double dsjeQn;

    private String maxTjTime;
}
