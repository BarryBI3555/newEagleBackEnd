package com.example.demo.service.impl;

import com.example.demo.entity.Group;
import com.example.demo.mapper.GroupMapper;
import com.example.demo.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private static final Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);

    @Autowired
    private GroupMapper groupMapper;

    @Override
    public List<Group> getGroups() {
        List<Group> allGroups = groupMapper.selectAllGroups();

        if (allGroups == null) {
            logger.warn("selectAllGroups 返回 null，返回空列表");
            return new ArrayList<>();
        }

        // 根据 groupscode 去重，保持插入顺序
        LinkedHashMap<String, Group> groupMap = new LinkedHashMap<>();
        for (Group group : allGroups) {
            if (group.getGroupscode() != null && !group.getGroupscode().isEmpty()) {
                groupMap.put(group.getGroupscode(), group);
            }
        }

        logger.info("去重后 {} 条小组记录", groupMap.size());
        return new ArrayList<>(groupMap.values());
    }
}