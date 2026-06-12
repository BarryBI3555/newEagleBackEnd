package com.example.demo.controller;

import java.util.List;

import com.example.demo.entity.Result;
import com.example.demo.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.entity.Group;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    private GroupService groupService;

    @GetMapping("/locations/groups")
    public Result<List<Group>> getGroups() {
        try {
            List<Group> groups = groupService.getGroups();
            logger.info("返回 {} 条小组记录", groups.size());
            return Result.success(groups);
        } catch (Exception e) {
            logger.error("获取小组列表失败: {}", e.getMessage(), e);
            return Result.error("获取小组列表失败: " + e.getMessage());
        }
    }
}
