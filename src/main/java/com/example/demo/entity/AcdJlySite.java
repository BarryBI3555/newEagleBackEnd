package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AcdJlySite {
    private Integer id;
    private String usercode;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Date reporttime;
}
