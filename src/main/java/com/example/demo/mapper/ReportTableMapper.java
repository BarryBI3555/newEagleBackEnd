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

}