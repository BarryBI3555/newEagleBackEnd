package com.example.demo.entity;

import lombok.Data;

/**
 * 汛期-物资信息 实体
 * 对应表: acd_items_rain
 */
@Data
public class RainItems {
    private Integer id;
    private String type;
    private String item;
    private Integer surplus;
    private Integer inv;
}
