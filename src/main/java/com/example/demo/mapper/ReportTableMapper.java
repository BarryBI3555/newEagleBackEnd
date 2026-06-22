package com.example.demo.mapper;

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
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;


@Mapper
public interface ReportTableMapper{

    String getMaxTjDateByTable(@Param("tableName") String tableName);

    /**
     * 按表名 + 业务标志（jaflag 值）取最大统计日期。
     * 用于同一张表被多个报表共用、按 jaflag 字段区分的场合（如 acd_rs_gzl_year）。
     * XML 中硬编码列名 jaflag，传参只需要表名 + jaflag 值。
     */
    String getMaxTjDateByTableAndFlag(@Param("tableName") String tableName,
                                      @Param("jaflag") String jaflag);

    List<CurGzlTableRy> getCurGzlData(@Param("startDate") String startDate,
                                      @Param("endDate") String endDate,
                                      @Param("comName") String comName,
                                      @Param("groups") String groups,
                                      @Param("userName") String userName);

    List<CurGzlTableBm> getCurGzlDataBm(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("comName") String comName
    );

    List<CurGzlTableGroup> getCurGzlDataGroup(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("comName") String comName,
            @Param("groups") String groups
    );

    List<CurGzlTableRs> getCurGzlDataRs(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("comName") String comName
    );

    // ==================== 新增表 ====================

    /** 周期-市公司 */
    List<AcdZhouqiQs> getZhouqiQsData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 周期-部门 */
    List<AcdZhouqiBm> getZhouqiBmData(
            @Param("tjDate") String tjDate,
            @Param("comname") String comname
    );

    /** 综合赔付率-客户群 */
    List<AcdZhpflKhq> getZhpflKhqData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 车险结案率-部门 */
    List<AcdPacllBm> getPacllBmData(
            @Param("tjDate") String tjDate,
            @Param("comname") String comname
    );

    /** 车险结案率-小组 */
    List<AcdPacllXz> getPacllXzData(
            @Param("tjDate") String tjDate,
            @Param("comname") String comname,
            @Param("groups") String groups
    );

    /** 车险结案率-人员 */
    List<AcdPacllRy> getPacllRyData(
            @Param("tjDate") String tjDate,
            @Param("bm") String bm,
            @Param("groups") String groups,
            @Param("username") String username
    );

    // ==================== 事故年赔付率 ====================

    /** 事故年赔付率-支公司 */
    List<AcdPflsgnZgs> getPflsgnZgsData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 事故年赔付率-客户群 */
    List<AcdPflsgnKhq> getPflsgnKhqData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 事故年赔付率-新能源 */
    List<AcdPflsgnXny> getPflsgnXnyData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    // ==================== 案均赔款 ====================

    /** 案均赔款-支公司（车险） */
    List<AcdAnjunCxZgs> getAnjunCxZgsData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 案均赔款-客户群（车险） */
    List<AcdAnjunCxKhq> getAnjunCxKhqData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 案均赔款-新能源（车险） */
    List<AcdAnjunCxXny> getAnjunCxXnyData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    // ==================== 年度每月系列 ====================

    /** 查勘量-年度每月 */
    List<AcdChakanYear> getChakanYearData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 定损提交量-年度每月 */
    List<AcdDingsunTjlYear> getDingsunTjlYearData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 定损完成量-年度每月 */
    List<AcdDingsunWclYear> getDingsunWclYearData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 查勘量+定损完成-年度每月 */
    List<AcdCkDswcYear> getCkDswcYearData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 定损支付量-年度每月 */
    List<AcdDingsunZflYear> getDingsunZflYearData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 理算量-年度每月 */
    List<AcdLisuanYear> getLisuanYearData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 人伤跟踪量-年度每月 */
    List<AcdRsGzlYear> getRsGzlYearData(
            @Param("tjDate") String tjDate
    );

    /** 人伤调解量-年度每月 */
    List<AcdRsGzlYear> getRsTjlYearData(
            @Param("tjDate") String tjDate
    );

    /** 查勘量-月度每日 */
    List<AcdChakanMonth> getChakanMonthData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 查勘量+定损完成-月度每日 */
    List<AcdCkDswcMonth> getCkDswcMonthData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 定损提交量-月度每日 */
    List<AcdDingsunTjlMonth> getDingsunTjlMonthData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 定损完成量-月度每日 */
    List<AcdDingsunWclMonth> getDingsunWclMonthData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 定损支付量-月度每日 */
    List<AcdDingsunZflMonth> getDingsunZflMonthData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    /** 人伤跟踪量-月度每日（jaflag=后续跟踪） */
    List<AcdRsGzlMonth> getRsGzlMonthData(@Param("tjDate") String tjDate);

    /** 人伤调解量-月度每日（jaflag=人伤调解） */
    List<AcdRsGzlMonth> getRsTjlMonthData(@Param("tjDate") String tjDate);

    /** 理算量-月度每日 */
    List<AcdLisuanMonth> getLisuanMonthData(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs
    );

    // ==================== 分页查询（laoxiao 9 + chakan_month） ====================

    /** 查勘量-年度每月 - 分页 */
    List<AcdChakanYear> getChakanYearDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countChakanYear(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 定损提交量-年度每月 - 分页 */
    List<AcdDingsunTjlYear> getDingsunTjlYearDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countDingsunTjlYear(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 定损完成量-年度每月 - 分页 */
    List<AcdDingsunWclYear> getDingsunWclYearDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countDingsunWclYear(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 查勘量+定损完成-年度每月 - 分页 */
    List<AcdCkDswcYear> getCkDswcYearDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countCkDswcYear(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 定损支付量-年度每月 - 分页 */
    List<AcdDingsunZflYear> getDingsunZflYearDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countDingsunZflYear(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 理算量-年度每月 - 分页 */
    List<AcdLisuanYear> getLisuanYearDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countLisuanYear(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 人伤跟踪量-年度每月 - 分页 */
    List<AcdRsGzlYear> getRsGzlYearDataPage(
            @Param("tjDate") String tjDate,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countRsGzlYear(@Param("tjDate") String tjDate);

    /** 人伤调解量-年度每月 - 分页 */
    List<AcdRsGzlYear> getRsTjlYearDataPage(
            @Param("tjDate") String tjDate,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countRsTjlYear(@Param("tjDate") String tjDate);

    /** 查勘量-月度每日 - 分页 */
    List<AcdChakanMonth> getChakanMonthDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countChakanMonth(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 查勘量+定损完成-月度每日 - 分页 */
    List<AcdCkDswcMonth> getCkDswcMonthDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countCkDswcMonth(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 定损提交量-月度每日 - 分页 */
    List<AcdDingsunTjlMonth> getDingsunTjlMonthDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countDingsunTjlMonth(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 定损完成量-月度每日 - 分页 */
    List<AcdDingsunWclMonth> getDingsunWclMonthDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countDingsunWclMonth(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 定损支付量-月度每日 - 分页 */
    List<AcdDingsunZflMonth> getDingsunZflMonthDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countDingsunZflMonth(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 人伤跟踪量-月度每日 - 分页 */
    List<AcdRsGzlMonth> getRsGzlMonthDataPage(
            @Param("tjDate") String tjDate,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countRsGzlMonth(@Param("tjDate") String tjDate);

    /** 人伤调解量-月度每日 - 分页 */
    List<AcdRsGzlMonth> getRsTjlMonthDataPage(
            @Param("tjDate") String tjDate,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countRsTjlMonth(@Param("tjDate") String tjDate);

    /** 理算量-月度每日 - 分页 */
    List<AcdLisuanMonth> getLisuanMonthDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countLisuanMonth(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    // ==================== 分页查询（lpcenter/efficiency 16 端点） ====================

    /** 人员日工作量 - 分页 */
    List<CurGzlTableRy> getCurGzlDataPage(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("comName") String comName,
            @Param("groups") String groups,
            @Param("userName") String userName,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countCurGzlData(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("comName") String comName,
            @Param("groups") String groups,
            @Param("userName") String userName);

    /** 部门日工作量 - 分页 */
    List<CurGzlTableBm> getCurGzlDataBmPage(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("comName") String comName,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countCurGzlDataBm(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("comName") String comName);

    /** 小组日工作量 - 分页 */
    List<CurGzlTableGroup> getCurGzlDataGroupPage(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("comName") String comName,
            @Param("groups") String groups,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countCurGzlDataGroup(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("comName") String comName,
            @Param("groups") String groups);

    /** 人伤日工作量 - 分页 */
    List<CurGzlTableRs> getCurGzlDataRsPage(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("comName") String comName,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countCurGzlDataRs(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("comName") String comName);

    /** 周期-市公司 - 分页 */
    List<AcdZhouqiQs> getZhouqiQsDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countZhouqiQs(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 周期-部门 - 分页 */
    List<AcdZhouqiBm> getZhouqiBmDataPage(
            @Param("tjDate") String tjDate,
            @Param("comname") String comname,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countZhouqiBm(
            @Param("tjDate") String tjDate,
            @Param("comname") String comname);

    /** 综合赔付率-客户群 - 分页 */
    List<AcdZhpflKhq> getZhpflKhqDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countZhpflKhq(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 车险结案率-部门 - 分页 */
    List<AcdPacllBm> getPacllBmDataPage(
            @Param("tjDate") String tjDate,
            @Param("comname") String comname,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countPacllBm(
            @Param("tjDate") String tjDate,
            @Param("comname") String comname);

    /** 车险结案率-小组 - 分页 */
    List<AcdPacllXz> getPacllXzDataPage(
            @Param("tjDate") String tjDate,
            @Param("comname") String comname,
            @Param("groups") String groups,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countPacllXz(
            @Param("tjDate") String tjDate,
            @Param("comname") String comname,
            @Param("groups") String groups);

    /** 车险结案率-人员 - 分页 */
    List<AcdPacllRy> getPacllRyDataPage(
            @Param("tjDate") String tjDate,
            @Param("bm") String bm,
            @Param("groups") String groups,
            @Param("username") String username,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countPacllRy(
            @Param("tjDate") String tjDate,
            @Param("bm") String bm,
            @Param("groups") String groups,
            @Param("username") String username);

    /** 事故年赔付率-支公司 - 分页 */
    List<AcdPflsgnZgs> getPflsgnZgsDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countPflsgnZgs(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 事故年赔付率-客户群 - 分页 */
    List<AcdPflsgnKhq> getPflsgnKhqDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countPflsgnKhq(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 事故年赔付率-新能源 - 分页 */
    List<AcdPflsgnXny> getPflsgnXnyDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countPflsgnXny(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 案均赔款-支公司（车险）- 分页 */
    List<AcdAnjunCxZgs> getAnjunCxZgsDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countAnjunCxZgs(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 案均赔款-客户群（车险）- 分页 */
    List<AcdAnjunCxKhq> getAnjunCxKhqDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countAnjunCxKhq(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

    /** 案均赔款-新能源（车险）- 分页 */
    List<AcdAnjunCxXny> getAnjunCxXnyDataPage(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs,
            @Param("offset") int offset,
            @Param("limit") int limit);
    long countAnjunCxXny(
            @Param("tjDate") String tjDate,
            @Param("comnameSgs") String comnameSgs);

}