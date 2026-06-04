package com.example.demo.entity;

import lombok.Data;

/**
 * 汛期-固定停车点位 实体
 * 对应表: acd_car_place_rain
 */
@Data
public class RainCarPlace {
    private Integer id;
    private String pName;
    private String address;
    private String region;
    private String conPerson;
    private String tel;
    private String tel1;
    private String remarks;
    private Double longitudeNew;
    private Double latitudeNew;
}
