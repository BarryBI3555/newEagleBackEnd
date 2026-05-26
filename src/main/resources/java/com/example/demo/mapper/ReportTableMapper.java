package com.example.demo.mapper;

import com.example.demo.entity.CurGzlTableRy;
import com.example.demo.entity.CurGzlTableBm;
import com.example.demo.entity.CurGzlTableGroup;
import com.example.demo.entity.CurGzlTableRs;
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

}