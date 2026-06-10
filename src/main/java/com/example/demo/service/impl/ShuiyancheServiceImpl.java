package com.example.demo.service.impl;

import com.example.demo.entity.AcdShuiyancheCldHz;
import com.example.demo.mapper.ShuiyancheMapper;
import com.example.demo.service.ShuiyancheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShuiyancheServiceImpl implements ShuiyancheService {

    @Autowired
    private ShuiyancheMapper shuiyancheMapper;

    @Override
    public AcdShuiyancheCldHz getCardData() {
        return shuiyancheMapper.getShuiyancheCardData("车险", "合计");
    }

    @Override
    public List<AcdShuiyancheCldHz> getReportTableData() {
        return shuiyancheMapper.getReportTableData();
    }
}
