package com.example.demo.service;

import com.example.demo.entity.Group;
import java.util.List;

public interface GroupService {

    /** 查询所有小组（按 groupscode 去重） */
    List<Group> getGroups();
}