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
import com.example.demo.entity.AcdCkDswcMonth;
import com.example.demo.entity.AcdDingsunTjlMonth;
import com.example.demo.entity.AcdDingsunWclMonth;
import com.example.demo.entity.AcdDingsunZflMonth;
import com.example.demo.entity.AcdRsGzlMonth;
import com.example.demo.entity.AcdLisuanMonth;
import com.example.demo.entity.PageResult;
import com.example.demo.mapper.ReportTableMapper;
import com.example.demo.service.ReportTableService;
import com.example.demo.util.TableNames;
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
        // 白名单校验：阻断 SQL 注入（XML 中 FROM ${tableName} 是字符串拼接）
        TableNames.requireValid(tableName);
        return reportTableMapper.getMaxTjDateByTable(tableName);
    }

    @Override
    public String getMaxTjDateByTableAndFlag(String tableName, String flagColumn, String flagValue) {
        // 白名单校验：表名 + 标志列均为 SQL 标识符，需双重白名单
        TableNames.requireValid(tableName);
        TableNames.requireValidFlagColumn(flagColumn);
        if (flagValue == null || flagValue.isEmpty()) {
            throw new IllegalArgumentException("flagValue 不能为空");
        }
        // mapper 层只支持 jaflag 列的专用查询；其它列名不在本接口范围内，统一拒绝
        if (!"jaflag".equals(flagColumn)) {
            throw new IllegalArgumentException("当前仅支持 flagColumn=jaflag，收到：" + flagColumn);
        }
        return reportTableMapper.getMaxTjDateByTableAndFlag(tableName, flagValue);
    }

    @Override
    public String getMaxTjDateByTableAndFlag(String tableName, String jaflag) {
        TableNames.requireValid(tableName);
        if (jaflag == null || jaflag.isEmpty()) {
            throw new IllegalArgumentException("jaflag 不能为空");
        }
        return reportTableMapper.getMaxTjDateByTableAndFlag(tableName, jaflag);
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

    @Override
    public List<AcdCkDswcMonth> getCkDswcMonthData(String tjDate, String comnameSgs) {
        return reportTableMapper.getCkDswcMonthData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdDingsunTjlMonth> getDingsunTjlMonthData(String tjDate, String comnameSgs) {
        return reportTableMapper.getDingsunTjlMonthData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdDingsunWclMonth> getDingsunWclMonthData(String tjDate, String comnameSgs) {
        return reportTableMapper.getDingsunWclMonthData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdDingsunZflMonth> getDingsunZflMonthData(String tjDate, String comnameSgs) {
        return reportTableMapper.getDingsunZflMonthData(tjDate, comnameSgs);
    }

    @Override
    public List<AcdRsGzlMonth> getRsGzlMonthData(String tjDate) {
        return reportTableMapper.getRsGzlMonthData(tjDate);
    }

    @Override
    public List<AcdRsGzlMonth> getRsTjlMonthData(String tjDate) {
        return reportTableMapper.getRsTjlMonthData(tjDate);
    }

    @Override
    public List<AcdLisuanMonth> getLisuanMonthData(String tjDate, String comnameSgs) {
        return reportTableMapper.getLisuanMonthData(tjDate, comnameSgs);
    }

    // ==================== 分页查询（laoxiao 9 + chakan_month） ====================

    private static int normalizeCurrent(int current) {
        return current < 1 ? 1 : current;
    }
    private static int normalizeSize(int size) {
        if (size < 1) return 20;
        if (size > 1000) return 1000;  // 防御性上限
        return size;
    }
    private static int offsetOf(int current, int size) {
        return (current - 1) * size;
    }

    @Override
    public PageResult<AcdChakanYear> getChakanYearDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countChakanYear(tjDate, comnameSgs);
        java.util.List<AcdChakanYear> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getChakanYearDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdDingsunTjlYear> getDingsunTjlYearDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countDingsunTjlYear(tjDate, comnameSgs);
        java.util.List<AcdDingsunTjlYear> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getDingsunTjlYearDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdDingsunWclYear> getDingsunWclYearDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countDingsunWclYear(tjDate, comnameSgs);
        java.util.List<AcdDingsunWclYear> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getDingsunWclYearDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdCkDswcYear> getCkDswcYearDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countCkDswcYear(tjDate, comnameSgs);
        java.util.List<AcdCkDswcYear> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getCkDswcYearDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdDingsunZflYear> getDingsunZflYearDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countDingsunZflYear(tjDate, comnameSgs);
        java.util.List<AcdDingsunZflYear> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getDingsunZflYearDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdLisuanYear> getLisuanYearDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countLisuanYear(tjDate, comnameSgs);
        java.util.List<AcdLisuanYear> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getLisuanYearDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdRsGzlYear> getRsGzlYearDataPage(String tjDate, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countRsGzlYear(tjDate);
        java.util.List<AcdRsGzlYear> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getRsGzlYearDataPage(tjDate, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdRsGzlYear> getRsTjlYearDataPage(String tjDate, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countRsTjlYear(tjDate);
        java.util.List<AcdRsGzlYear> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getRsTjlYearDataPage(tjDate, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdChakanMonth> getChakanMonthDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countChakanMonth(tjDate, comnameSgs);
        java.util.List<AcdChakanMonth> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getChakanMonthDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdCkDswcMonth> getCkDswcMonthDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countCkDswcMonth(tjDate, comnameSgs);
        java.util.List<AcdCkDswcMonth> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getCkDswcMonthDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdDingsunTjlMonth> getDingsunTjlMonthDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countDingsunTjlMonth(tjDate, comnameSgs);
        java.util.List<AcdDingsunTjlMonth> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getDingsunTjlMonthDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdDingsunWclMonth> getDingsunWclMonthDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countDingsunWclMonth(tjDate, comnameSgs);
        java.util.List<AcdDingsunWclMonth> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getDingsunWclMonthDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdDingsunZflMonth> getDingsunZflMonthDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countDingsunZflMonth(tjDate, comnameSgs);
        java.util.List<AcdDingsunZflMonth> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getDingsunZflMonthDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdRsGzlMonth> getRsGzlMonthDataPage(String tjDate, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countRsGzlMonth(tjDate);
        java.util.List<AcdRsGzlMonth> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getRsGzlMonthDataPage(tjDate, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdRsGzlMonth> getRsTjlMonthDataPage(String tjDate, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countRsTjlMonth(tjDate);
        java.util.List<AcdRsGzlMonth> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getRsTjlMonthDataPage(tjDate, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdLisuanMonth> getLisuanMonthDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countLisuanMonth(tjDate, comnameSgs);
        java.util.List<AcdLisuanMonth> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getLisuanMonthDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    // ==================== 分页查询（lpcenter/efficiency 16 端点） ====================

    @Override
    public PageResult<CurGzlTableRy> getCurGzlDataPage(String startDate, String endDate, String comName, String groups, String userName, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countCurGzlData(startDate, endDate, comName, groups, userName);
        java.util.List<CurGzlTableRy> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getCurGzlDataPage(startDate, endDate, comName, groups, userName, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<CurGzlTableBm> getCurGzlDataBmPage(String startDate, String endDate, String comName, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countCurGzlDataBm(startDate, endDate, comName);
        java.util.List<CurGzlTableBm> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getCurGzlDataBmPage(startDate, endDate, comName, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<CurGzlTableGroup> getCurGzlDataGroupPage(String startDate, String endDate, String comName, String groups, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countCurGzlDataGroup(startDate, endDate, comName, groups);
        java.util.List<CurGzlTableGroup> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getCurGzlDataGroupPage(startDate, endDate, comName, groups, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<CurGzlTableRs> getCurGzlDataRsPage(String startDate, String endDate, String comName, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countCurGzlDataRs(startDate, endDate, comName);
        java.util.List<CurGzlTableRs> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getCurGzlDataRsPage(startDate, endDate, comName, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdZhouqiQs> getZhouqiQsDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countZhouqiQs(tjDate, comnameSgs);
        java.util.List<AcdZhouqiQs> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getZhouqiQsDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdZhouqiBm> getZhouqiBmDataPage(String tjDate, String comname, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countZhouqiBm(tjDate, comname);
        java.util.List<AcdZhouqiBm> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getZhouqiBmDataPage(tjDate, comname, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdZhpflKhq> getZhpflKhqDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countZhpflKhq(tjDate, comnameSgs);
        java.util.List<AcdZhpflKhq> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getZhpflKhqDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdPacllBm> getPacllBmDataPage(String tjDate, String comname, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countPacllBm(tjDate, comname);
        java.util.List<AcdPacllBm> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getPacllBmDataPage(tjDate, comname, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdPacllXz> getPacllXzDataPage(String tjDate, String comname, String groups, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countPacllXz(tjDate, comname, groups);
        java.util.List<AcdPacllXz> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getPacllXzDataPage(tjDate, comname, groups, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdPacllRy> getPacllRyDataPage(String tjDate, String bm, String groups, String username, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countPacllRy(tjDate, bm, groups, username);
        java.util.List<AcdPacllRy> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getPacllRyDataPage(tjDate, bm, groups, username, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdPflsgnZgs> getPflsgnZgsDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countPflsgnZgs(tjDate, comnameSgs);
        java.util.List<AcdPflsgnZgs> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getPflsgnZgsDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdPflsgnKhq> getPflsgnKhqDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countPflsgnKhq(tjDate, comnameSgs);
        java.util.List<AcdPflsgnKhq> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getPflsgnKhqDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdPflsgnXny> getPflsgnXnyDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countPflsgnXny(tjDate, comnameSgs);
        java.util.List<AcdPflsgnXny> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getPflsgnXnyDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdAnjunCxZgs> getAnjunCxZgsDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countAnjunCxZgs(tjDate, comnameSgs);
        java.util.List<AcdAnjunCxZgs> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getAnjunCxZgsDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdAnjunCxKhq> getAnjunCxKhqDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countAnjunCxKhq(tjDate, comnameSgs);
        java.util.List<AcdAnjunCxKhq> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getAnjunCxKhqDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }

    @Override
    public PageResult<AcdAnjunCxXny> getAnjunCxXnyDataPage(String tjDate, String comnameSgs, int current, int size) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        long total = reportTableMapper.countAnjunCxXny(tjDate, comnameSgs);
        java.util.List<AcdAnjunCxXny> records = total == 0
                ? java.util.Collections.emptyList()
                : reportTableMapper.getAnjunCxXnyDataPage(tjDate, comnameSgs, offsetOf(current, size), size);
        return new PageResult<>(records, total, current, size);
    }
}