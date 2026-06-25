package com.example.demo.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 自定义 TypeHandler，处理 GBase JDBC 驱动返回的百分比格式字符串。
 *
 * GBase 的 JDBC 驱动在返回 decimal 字段时，会自动将数值转为
 * 带百分号的字符串（如 '-50.42%'），而 Java 实体类期望接收 Double。
 * 本 TypeHandler 透明地将这类字符串转换为 Double。
 *
 * 使用方法：
 * 1. 全局配置（MyBatis Config）：自动应用于所有 Double 类型
 * 2. 字段级注解：@Column(jdbcType=DECIMAL, typeHandler=PercentageDoubleTypeHandler.class)
 */
@MappedJdbcTypes(value = {JdbcType.DECIMAL, JdbcType.VARCHAR, JdbcType.CHAR})
@MappedTypes(Double.class)
public class PercentageDoubleTypeHandler extends BaseTypeHandler<Double> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Double parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setDouble(i, parameter);
    }

    @Override
    public Double getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseDouble(rs.getString(columnName));
    }

    @Override
    public Double getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseDouble(rs.getString(columnIndex));
    }

    @Override
    public Double getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseDouble(cs.getString(columnIndex));
    }

    /**
     * 解析百分比字符串或普通数字字符串为 Double。
     *
     * 支持格式：
     * - 纯数字："-50.42" → -50.42
     * - 百分比："-50.42%" → -0.5042
     * - null/空字符串 → null
     */
    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        value = value.trim();
        try {
            if (value.endsWith("%")) {
                // 去掉百分号，转换为小数（-50.42% → -0.5042）
                String numericPart = value.substring(0, value.length() - 1).trim();
                return Double.parseDouble(numericPart) / 100.0;
            } else {
                return Double.parseDouble(value);
            }
        } catch (NumberFormatException e) {
            // 日志记录或返回默认值
            return null;
        }
    }
}
