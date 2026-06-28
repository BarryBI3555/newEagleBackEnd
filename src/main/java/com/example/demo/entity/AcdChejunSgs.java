package com.example.demo.entity;

import lombok.Data;

/**
 * 车均定损-市公司 实体
 * 对应表: acd_chejun_sgs
 */
@Data
public class AcdChejunSgs {

    private String tjdate;

    private String comcodeSgs;

    private String comnameSgs;

    private Double ajsBn;
    private Double ajsTb;
    private Double dsjeBn;
    private Double dsjeTb;
    private Double cjBn;
    private Double cjTb;
    private Double lwnCjBn;
    private Double lwnCjTb;
    private Double lwysCjBn;
    private Double lwysCjTb;
    private Double hjcjBn;
    private Double hjcjTb;
    private Double gscjBn;
    private Double gscjTb;
    private Double hxb;
    private Double hxbTb;
    private Double ajsQn;
    private Double dsjeQn;
    private Double cjQn;
    private Double lwnCjQn;
    private Double lwysCjQn;
    private Double hjcjQn;
    private Double gscjQn;
    private Double hxbQn;

    private String maxTjTime;
}
