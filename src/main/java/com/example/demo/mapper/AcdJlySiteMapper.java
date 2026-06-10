package com.example.demo.mapper;

import com.example.demo.entity.AcdJlySite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AcdJlySiteMapper {
    int countByUsercodeAndReporttime(@Param("usercode") String usercode,
                                     @Param("reporttime") String reporttime);
    Integer getMaxId();
    int batchInsert(@Param("list") List<AcdJlySite> list);
}
