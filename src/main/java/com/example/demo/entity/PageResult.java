package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用分页结果包装。
 *
 * <p>后端分页端点（{@code /<path>/page}）统一返回此结构。
 * 字段命名与前端 {@code useTable} hook 期望的 {@code { records, total, current, size }}
 * 形态一致，可被前端直接消费，无需在 apiFn 中再手动 slice。</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /** 当前页数据 */
    private List<T> records;

    /** 总记录数（与 current/size 无关） */
    private long total;

    /** 当前页码（1-based） */
    private int current;

    /** 每页大小 */
    private int size;
}
