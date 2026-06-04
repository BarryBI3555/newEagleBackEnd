package com.example.demo.entity;

import lombok.Data;

/**
 * 汛期-预警对应措施 实体
 * 关联路径: acd_day_level_rain.tjdate → acd_level_map_rain.id → acd_level_process_rain.number
 * 一次取某一天所有对应措施（按 number 升序）
 */
@Data
public class RainLevelProcess {
    private Integer number;
    private String measure;
}
