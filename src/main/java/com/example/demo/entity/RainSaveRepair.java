package com.example.demo.entity;

import lombok.Data;

/**
 * 汛期-施救单位 实体
 * 对应表: acd_save_repair_rain
 */
@Data
public class RainSaveRepair {
    private Integer id;
    private String name;
    private String conPerson;
    private String tel;
    private String region;
    private String scopeS;
    private String remarks;
}
