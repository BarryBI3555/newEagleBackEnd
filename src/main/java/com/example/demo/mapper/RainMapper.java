package com.example.demo.mapper;

import com.example.demo.entity.RainDayLevel;
import com.example.demo.entity.RainCarPlace;
import com.example.demo.entity.RainZhiban;
import com.example.demo.entity.RainSaveRepair;
import com.example.demo.entity.RainLianluo;
import com.example.demo.entity.RainItems;
import com.example.demo.entity.RainLevelProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RainMapper {

    /** 每日等级预警（按文档：join level 表取 rank/warning/meteor） */
    List<RainDayLevel> getDayLevel(@Param("today") String today);

    /** 固定停车点位 */
    List<RainCarPlace> getCarPlace();

    /** 值班信息（仅当天） */
    List<RainZhiban> getZhiban(@Param("today") String today);

    /** 施救单位 */
    List<RainSaveRepair> getRepair();

    /** 中心对口联络机制 */
    List<RainLianluo> getLianluo();

    /** 物资 */
    List<RainItems> getItems();

    /**
     * 某日期预警对应的全部措施（按 number 升序）
     * 联表路径：acd_day_level_rain(tjdate) → acd_level_map_rain(id) → acd_level_process_rain(number)
     */
    List<RainLevelProcess> getLevelProcessByDate(@Param("date") String date);
}
