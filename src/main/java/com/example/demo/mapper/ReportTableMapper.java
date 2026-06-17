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
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;


@Mapper
public interface ReportTableMapper{

    String getMaxTjDateByTable(@Param("tableName") String tableName);

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

}