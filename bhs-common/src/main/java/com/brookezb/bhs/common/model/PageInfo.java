package com.brookezb.bhs.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author brooke_zb
 */
@Data
@NoArgsConstructor
public class PageInfo<T> {
    /**
     * 当前页
     */
    private int currentPage;

    /**
     * 每页的数量
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int pageCount;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 结果集
     */
    private List<T> list;

    public PageInfo(int currentPage, int pageSize, long total) {
        this(currentPage, pageSize, total, null);
    }

    public PageInfo(int currentPage, int pageSize, long total, List<T> list) {
        this(currentPage, pageSize, (int) Math.ceil((double) total / pageSize), total, list);
    }

    public PageInfo(int currentPage, int pageSize, int pageCount, long total, List<T> list) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.pageCount = pageCount;
        this.total = total;
        this.list = list;
    }
}
