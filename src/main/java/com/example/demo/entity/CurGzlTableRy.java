package com.example.demo.entity;

import lombok.Data;

@Data
public class CurGzlTableRy {
    
    private Long id;

    private String comCode; // 部门编码

    private String comName; // 部门
    
    private String userName; // 人员
    
    private String userCode; // 用户编码
    
    private String groups; // 小组
    
    private String groupsCode; // 小组编码
    
    private Integer ckJsl; // 查勘接收量
    
    private Integer ckJslWcl; // 查勘接收完成数量
    
    private Integer ckWcl; // 查勘完成量
    
    private Integer dsTjl; // 定损提交量
    
    private Integer dsWcl; // 定损完成量
    
    private Integer dsZfl; // 定损支付量
    
    private Integer shouGen; // 首跟数量
    
    private Integer houGen; // 后跟数量
    
    private Integer tiaoJie; // 调解数量
    
    private Integer ja; // 结案数量
    
    private Integer zl; // 总量
    
    private String tjDate; // 统计日期

    private String maxTjTime; // 统计时间
}