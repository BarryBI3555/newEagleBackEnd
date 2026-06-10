package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AcdShuiyancheCldHz {
    private String tjdate;
    private String areaflag;
    private String codecname;
    private BigDecimal bal_lj;
    private BigDecimal sumestipaid;
    private BigDecimal ja_lj;
    private BigDecimal sumpaid_lj;
    private String bal_dr;
    private String sumestipaid_dr;
}
