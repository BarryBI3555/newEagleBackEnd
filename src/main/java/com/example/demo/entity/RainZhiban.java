package com.example.demo.entity;

import lombok.Data;

/**
 * 汛期-值班信息 实体
 * 对应表: acd_zhiban_rain
 */
@Data
public class RainZhiban {
    private String tjDate;
    private String nameWorker;
    private String nameLeader;
}
