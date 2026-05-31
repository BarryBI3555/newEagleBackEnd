package com.example.demo.entity;

import lombok.Data;

/**
 * 综合赔付率-客户群 实体
 * 对应表: acd_zhpfl_khq
 */
@Data
public class AcdZhpflKhq {

    private Long id;

    private String tjDate;          // 统计日期

    private String comcodeSgs;      // 市公司编码

    private String comnameSgs;      // 市公司名称

    private String khq;             // 客户群

    private String riskcode;        // 险种代码

    private Double jbf;             // 件保费

    private Double pfcb;            // 赔付成本

    private Double fy;              // 费用

    private Double glfy;            // 管理费用

    private Double cblr;            // 成本率

    private String zhcbl;           // 综合成本率

    private String zhfyl;           // 综合费用率

    private String zhpfl;           // 综合赔付率

    private Double jbfQn;           // 件保费(去年)

    private Double pfcbQn;          // 赔付成本(去年)

    private Double fyQn;            // 费用(去年)

    private Double glfyQn;          // 管理费用(去年)

    private Double cblrQn;          // 成本率(去年)

    private String zhcblQn;         // 综合成本率(去年)

    private String zhfylQn;         // 综合费用率(去年)

    private String zhpflQn;         // 综合赔付率(去年)

    private String zhcblTb;         // 综合成本率(同比)

    private String zhfylTb;         // 综合费用率(同比)

    private String zhpflTb;         // 综合赔付率(同比)

    private String maxTjTime;       // 最大统计时间（用于前端标题展示）
}