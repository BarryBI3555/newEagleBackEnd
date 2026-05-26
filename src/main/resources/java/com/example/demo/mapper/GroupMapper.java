package com.example.demo.mapper;

import com.example.demo.entity.Group;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface GroupMapper {

    List<Group> selectAllGroups();
}