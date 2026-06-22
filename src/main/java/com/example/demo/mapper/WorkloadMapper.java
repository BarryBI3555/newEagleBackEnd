package com.example.demo.mapper;

import com.example.demo.entity.CurGzlTableBm;
import com.example.demo.entity.CurGzlTableGroup;
import com.example.demo.entity.CurGzlTableRy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkloadMapper {

    /** 获取最大统计日期（通用） */
    String getMaxTjDateByTable(@Param("tableName") String tableName);

    /** 部门级工作量（按日粒度查询，Java 层聚合月/周） */
    List<CurGzlTableBm> getWorkloadBmData(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("comName") String comName
    );

    /** 小组级工作量（按日粒度查询，Java 层聚合月/周） */
    List<CurGzlTableGroup> getWorkloadGroupData(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("comCode") String comCode,
            @Param("groups") String groups
    );

    /** 员工级工作量（按日粒度查询，Java 层聚合月/周） */
    List<CurGzlTableRy> getWorkloadRyData(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("groupsCode") String groupsCode,
            @Param("userName") String userName
    );

    /** 根据部门名称获取部门编码 */
    String getComCodeByName(@Param("comName") String comName);

    /** 根据部门编码 + 小组名称获取小组编码 */
    String getGroupsCodeByComAndName(@Param("comCode") String comCode, @Param("groupsName") String groupsName);
}