package com.example.demo.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 允许通过 {@code getMaxTjDateByTable} 动态查询的表名白名单。
 *
 * <p>Mapper XML 中 {@code FROM ${tableName}} 使用字符串拼接以支持动态表名，
 * 这是 MyBatis 处理 SQL 标识符（表/列名）所必需的——{@code #{}} 仅支持值参数，
 * 不支持标识符。任何不在白名单中的表名都会抛出 {@link IllegalArgumentException}，
 * 防止 SQL 注入风险。</p>
 *
 * <p>新增可访问的统计表时，需在此处同步加入白名单。</p>
 */
public final class TableNames {

    private TableNames() {}

    private static final Set<String> ALLOWED = Collections.unmodifiableSet(
        new HashSet<>(Arrays.asList(
            // 当日工作量
            "acd_dangri_gzl_ry",
            "acd_dangri_gzl_bm",
            "acd_dangri_gzl_group",
            "acd_dangri_gzl_rs",
            // 周期
            "acd_zhouqi_qs",
            "acd_zhouqi_bm",
            // 综合赔付率
            "acd_zhpfl_khq",
            // 车险结案率
            "acd_pacll_bm",
            "acd_pacll_xz",
            "acd_pacll_ry",
            // 事故年赔付率
            "acd_pflsgn_zgs",
            "acd_pflsgn_khq",
            "acd_pflsgn_xny",
            // 案均赔款（车险）
            "acd_anjun_cx_zgs",
            "acd_anjun_cx_khq",
            "acd_anjun_cx_xny",
            // 年度每月
            "acd_chakan_year",
            "acd_dingsun_tjl_year",
            "acd_dingsun_wcl_year",
            "acd_ck_dswc_year",
            "acd_dingsun_zfl_year",
            "acd_lisuan_year",
            "acd_rs_gzl_year",
            // 月度每日
            "acd_chakan_month",
            "acd_ck_dswc_month",
            "acd_dingsun_tjl_month",
            "acd_dingsun_wcl_month",
            "acd_dingsun_zfl_month",
            "acd_lisuan_month",
            "acd_rs_gzl_month"
        ))
    );

    public static boolean isAllowed(String tableName) {
        return tableName != null && ALLOWED.contains(tableName);
    }

    /**
     * 校验表名是否在白名单中。
     *
     * @throws IllegalArgumentException 当表名为 null 或不在白名单时
     */
    public static void requireValid(String tableName) {
        if (!isAllowed(tableName)) {
            throw new IllegalArgumentException("不允许的表名: " + tableName);
        }
    }

    /**
     * 允许通过 {@code getMaxTjDateByTableAndFlag} 动态拼接的"业务标志列"白名单。
     * 同样是 SQL 标识符，禁止任意外部输入。
     */
    private static final Set<String> ALLOWED_FLAG_COLUMNS = Collections.unmodifiableSet(
        new HashSet<>(Arrays.asList("jaflag"))
    );

    public static boolean isFlagColumnAllowed(String column) {
        return column != null && ALLOWED_FLAG_COLUMNS.contains(column);
    }

    public static void requireValidFlagColumn(String column) {
        if (!isFlagColumnAllowed(column)) {
            throw new IllegalArgumentException("不允许的标志列: " + column);
        }
    }
}
