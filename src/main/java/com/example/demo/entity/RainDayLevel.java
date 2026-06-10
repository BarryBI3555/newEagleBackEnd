package com.example.demo.entity;

import lombok.Data;

/**
 * 汛期-每日等级预警 实体
 * 对应表: acd_day_level_rain ⨝ acd_level_rain
 */
@Data
public class RainDayLevel {
    private Integer id;
    private String tjDate;
    private String rank;
    private String warning;
    private String meteor;
}
