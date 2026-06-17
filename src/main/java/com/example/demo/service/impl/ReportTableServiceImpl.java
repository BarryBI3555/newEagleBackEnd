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
import com.example.demo.entity.AcdPflsgnZgs;
import com.example.demo.entity.AcdPflsgnKhq;
import com.example.demo.entity.AcdPflsgnXny;
import com.example.demo.entity.AcdAnjunCxZgs;
import com.example.demo.entity.AcdAnjunCxKhq;
import com.example.demo.entity.AcdAnjunCxXny;
import com.example.demo.entity.AcdChakanYear;
import com.example.demo.entity.AcdDingsunTjlYear;
import com.example.demo.entity.AcdDingsunWclYear;
import com.example.demo.entity.AcdCkDswcYear;
import com.example.demo.entity.AcdDingsunZflYear;
import com.example.demo.entity.AcdLisuanYear;
import com.example.demo.entity.AcdRsGzlYear;
import com.example.demo.entity.AcdChakanMonth;
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

    // ==================== 事故年赔付率 ====================

    @Override
    public List<AcdPflsgnZgs> getPflsgnZgsData(String tjDate, String comnameSgs) {
        return reportTableMapper.getPflsgnZgsData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdPflsgnKhq> getPflsgnKhqData(String tjDate, String comnameSgs) {
        return reportTableMapper.getPflsgnKhqData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdPflsgnXny> getPflsgnXnyData(String tjDate, String comnameSgs) {
        return reportTableMapper.getPflsgnXnyData(tjDate, comnameSgs);
    }

    // ==================== 案均赔款 ====================

    @Override
    public List<AcdAnjunCxZgs> getAnjunCxZgsData(String tjDate, String comnameSgs) {
        return reportTableMapper.getAnjunCxZgsData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdAnjunCxKhq> getAnjunCxKhqData(String tjDate, String comnameSgs) {
        return reportTableMapper.getAnjunCxKhqData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdAnjunCxXny> getAnjunCxXnyData(String tjDate, String comnameSgs) {
        return reportTableMapper.getAnjunCxXnyData(tjDate, comnameSgs);
    }

    // ==================== 年度每月系列 ====================

    @Override
    public List<AcdChakanYear> getChakanYearData(String tjDate, String comnameSgs) {
        return reportTableMapper.getChakanYearData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdDingsunTjlYear> getDingsunTjlYearData(String tjDate, String comnameSgs) {
        return reportTableMapper.getDingsunTjlYearData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdDingsunWclYear> getDingsunWclYearData(String tjDate, String comnameSgs) {
        return reportTableMapper.getDingsunWclYearData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdCkDswcYear> getCkDswcYearData(String tjDate, String comnameSgs) {
        return reportTableMapper.getCkDswcYearData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdDingsunZflYear> getDingsunZflYearData(String tjDate, String comnameSgs) {
        return reportTableMapper.getDingsunZflYearData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdLisuanYear> getLisuanYearData(String tjDate, String comnameSgs) {
        return reportTableMapper.getLisuanYearData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdRsGzlYear> getRsGzlYearData(String tjDate) {
        return reportTableMapper.getRsGzlYearData(tjDate);
    }

    @Override
    public List<AcdRsGzlYear> getRsTjlYearData(String tjDate) {
        return reportTableMapper.getRsTjlYearData(tjDate);
    }

    @Override
    public List<AcdChakanMonth> getChakanMonthData(String tjDate, String comnameSgs) {
        return reportTableMapper.getChakanMonthData(tjDate, comnameSgs);
    }
}