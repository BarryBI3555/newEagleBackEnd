package com.example.demo.mapper;

import com.example.demo.entity.AcdShuiyancheCldHz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShuiyancheMapper {
    AcdShuiyancheCldHz getShuiyancheCardData(@Param("areaflag") String areaflag, @Param("codecname") String codecname, @Param("today") String today);

    List<AcdShuiyancheCldHz> getReportTableData(@Param("today") String today);
}
