package com.example.demo.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.entity.Group;
import com.example.demo.mapper.GroupMapper;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    private GroupMapper groupMapper;

    @GetMapping("/locations/groups")
    public Result<List<Group>> getGroups() {
        try {
            // 检查 mapper 是否注入成功
            if (groupMapper == null) {
                logger.error("GroupMapper 未注入");
                return Result.error("系统配置错误：GroupMapper 未注入");
            }

            List<Group> allGroups = groupMapper.selectAllGroups();
            logger.info("查询到 {} 条小组记录", allGroups != null ? allGroups.size() : 0);

            // 防空：如果数据库表不存在或返回 null
            if (allGroups == null) {
                logger.warn("selectAllGroups 返回 null，返回空列表");
                return Result.success(new ArrayList<>());
            }

            // 根据groupscode去重
            Map<String, Group> groupMap = new LinkedHashMap<>();
            for (Group group : allGroups) {
                if (group.getGroupscode() != null && !group.getGroupscode().isEmpty()) {
                    groupMap.put(group.getGroupscode(), group);
                }
            }
            logger.info("去重后 {} 条小组记录", groupMap.size());
            return Result.success(new ArrayList<>(groupMap.values()));
        } catch (Exception e) {
            logger.error("获取小组列表失败: {}", e.getMessage(), e);
            return Result.error("获取小组列表失败: " + e.getMessage());
        }
    }
}
