package com.alivold.domain;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public class PageResult<T> {
    private long current;  // 当前页
    private long size;     // 每页条数
    private long total;    // 总记录数
    private List<T> records;  // 当前页的数据

    public PageResult(Page<T> page) {
        this.current = page.getCurrent();
        this.size = page.getSize();
        this.total = page.getTotal();
        this.records = page.getRecords();
    }

    // Getter 和 Setter 方法
    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
