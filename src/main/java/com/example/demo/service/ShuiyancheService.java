package com.example.demo.service;

import com.example.demo.entity.AcdShuiyancheCldHz;
import java.util.List;

public interface ShuiyancheService {
    AcdShuiyancheCldHz getCardData();
    List<AcdShuiyancheCldHz> getReportTableData();
}
