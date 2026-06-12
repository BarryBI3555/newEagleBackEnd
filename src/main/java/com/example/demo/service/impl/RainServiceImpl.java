package com.example.demo.service.impl;

import com.example.demo.entity.RainDayLevel;
import com.example.demo.entity.RainCarPlace;
import com.example.demo.entity.RainZhiban;
import com.example.demo.entity.RainSaveRepair;
import com.example.demo.entity.RainLianluo;
import com.example.demo.entity.RainItems;
import com.example.demo.entity.RainLevelProcess;
import com.example.demo.entity.AcdShuiyancheCldHz;
import com.example.demo.mapper.RainMapper;
import com.example.demo.service.RainService;
import com.example.demo.service.ShuiyancheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RainServiceImpl implements RainService {

    @Autowired
    private RainMapper rainMapper;

    @Autowired
    private ShuiyancheService shuiyancheService;

    @Override
    public List<RainDayLevel> getDayLevel(String today) {
        return rainMapper.getDayLevel(today);
    }

    @Override
    public List<RainCarPlace> getCarPlace() {
        return rainMapper.getCarPlace();
    }

    @Override
    public List<RainZhiban> getZhiban(String today) {
        return rainMapper.getZhiban(today);
    }

    @Override
    public List<RainSaveRepair> getRepair() {
        return rainMapper.getRepair();
    }

    @Override
    public List<RainLianluo> getLianluo() {
        return rainMapper.getLianluo();
    }

    @Override
    public List<RainItems> getItems() {
        return rainMapper.getItems();
    }

    @Override
    public List<RainLevelProcess> getLevelProcessByDate(String date) {
        return rainMapper.getLevelProcessByDate(date);
    }

    @Override
    public AcdShuiyancheCldHz getCardData() {
        return shuiyancheService.getCardData();
    }

    @Override
    public List<AcdShuiyancheCldHz> getReportTableData() {
        return shuiyancheService.getReportTableData();
    }
}