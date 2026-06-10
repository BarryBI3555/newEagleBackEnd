package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AcdOldCaseRainXq {
    private Integer id;
    private String makecom;
    private String reportdate;
    private String reporthour;
    private String damagename;
    private String damageaddress;
    private String checkaddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
}