package com.example.demo.service;

import com.example.demo.entity.CurGzlTableRy;
import com.example.demo.entity.CurGzlTableBm;
import com.example.demo.entity.CurGzlTableRs;
import com.example.demo.entity.CurGzlTableGroup;
import com.example.demo.entity.AcdZhouqiQs;
import com.example.demo.entity.AcdZhouqiBm;
import com.example.demo.entity.AcdZhpflKhq;
import com.example.demo.entity.AcdPacllBm;
import com.example.demo.entity.AcdPacllXz;
import com.example.demo.entity.AcdPacllRy;

import java.util.List;

public interface ReportTableService {
    // 通用获取最大日期
    String getMaxTjDate(String tableName);

    List<CurGzlTableRy> getCurGzlData(String startDate, String endDate, String comName, String groups, String userName);

    // 新增：按部门统计
    List<CurGzlTableBm> getCurGzlDataBm(String startDate, String endDate, String comName);

    // 小组统计表
    List<CurGzlTableGroup> getCurGzlDataGroup(
            String startDate,
            String endDate,
            String comName,
            String groups
    );

    // 人员/住院门诊统计表（新表）
    List<CurGzlTableRs> getCurGzlDataRs(String startDate, String endDate, String comName);

    // ==================== 新增表接口 ====================

    /** 周期-市公司 */
    List<AcdZhouqiQs> getZhouqiQsData(String tjDate, String comnameSgs);

    /** 周期-部门 */
    List<AcdZhouqiBm> getZhouqiBmData(String tjDate, String comname);

    /** 综合赔付率-客户群 */
    List<AcdZhpflKhq> getZhpflKhqData(String tjDate, String comnameSgs);

    /** 车险结案率-部门 */
    List<AcdPacllBm> getPacllBmData(String tjDate, String comname);

    /** 车险结案率-小组 */
    List<AcdPacllXz> getPacllXzData(String tjDate, String comname, String groups);

    /** 车险结案率-人员 */
    List<AcdPacllRy> getPacllRyData(String tjDate, String bm, String groups, String username);
}