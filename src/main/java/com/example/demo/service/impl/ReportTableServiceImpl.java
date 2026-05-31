package com.example.demo.service.impl;

import com.example.demo.entity.CurGzlTableRy;
import com.example.demo.entity.CurGzlTableBm;
import com.example.demo.entity.CurGzlTableGroup;
import com.example.demo.entity.CurGzlTableRs;
import com.example.demo.entity.AcdZhouqiQs;
import com.example.demo.entity.AcdZhouqiBm;
import com.example.demo.entity.AcdZhpflKhq;
import com.example.demo.entity.AcdPacllBm;
import com.example.demo.entity.AcdPacllXz;
import com.example.demo.entity.AcdPacllRy;
import com.example.demo.mapper.ReportTableMapper;
import com.example.demo.service.ReportTableService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class ReportTableServiceImpl implements ReportTableService {

    @Resource
    private ReportTableMapper reportTableMapper;

    // 通用
    @Override
    public String getMaxTjDate(String tableName) {
        return reportTableMapper.getMaxTjDateByTable(tableName);
    }

    @Override
    public List<CurGzlTableRy> getCurGzlData(String startDate, String endDate, String comName, String groups, String userName) {
        return reportTableMapper.getCurGzlData(startDate, endDate, comName, groups, userName);
    }

    @Override
    public List<CurGzlTableBm> getCurGzlDataBm(String startDate, String endDate, String comName) {
        return reportTableMapper.getCurGzlDataBm(startDate, endDate, comName);
    }

    @Override
    public List<CurGzlTableGroup> getCurGzlDataGroup(String startDate, String endDate, String comName, String groups) {
        return reportTableMapper.getCurGzlDataGroup(startDate, endDate, comName, groups);
    }

    @Override
    public List<CurGzlTableRs> getCurGzlDataRs(String startDate, String endDate, String comName) {
        return reportTableMapper.getCurGzlDataRs(startDate, endDate, comName);
    }

    // ==================== 新增表实现 ====================

    @Override
    public List<AcdZhouqiQs> getZhouqiQsData(String tjDate, String comnameSgs) {
        return reportTableMapper.getZhouqiQsData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdZhouqiBm> getZhouqiBmData(String tjDate, String comname) {
        return reportTableMapper.getZhouqiBmData(tjDate, comname);
    }

    @Override
    public List<AcdZhpflKhq> getZhpflKhqData(String tjDate, String comnameSgs) {
        return reportTableMapper.getZhpflKhqData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdPacllBm> getPacllBmData(String tjDate, String comname) {
        return reportTableMapper.getPacllBmData(tjDate, comname);
    }

    @Override
    public List<AcdPacllXz> getPacllXzData(String tjDate, String comname, String groups) {
        return reportTableMapper.getPacllXzData(tjDate, comname, groups);
    }

    @Override
    public List<AcdPacllRy> getPacllRyData(String tjDate, String bm, String groups, String username) {
        return reportTableMapper.getPacllRyData(tjDate, bm, groups, username);
    }
}