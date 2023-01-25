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
    private int pageNum;

    /**
     * 每页的数量
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 结果集
     */
    private List<T> list;

    public PageInfo(int pageNum, int pageSize, long total) {
        this(pageNum, pageSize, total, null);
    }

    public PageInfo(int pageNum, int pageSize, long total, List<T> list) {
        this(pageNum, pageSize, (int) Math.ceil((double) total / pageSize), total, list);
    }

    public PageInfo(int pageNum, int pageSize, int pages, long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pages;
        this.total = total;
        this.list = list;
    }
}
