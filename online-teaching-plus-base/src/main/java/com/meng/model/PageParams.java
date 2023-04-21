package com.meng.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author 梦举
 * @version 1.0
 * @description TODO
 * @date 2023/3/9 14:30
 */

@Data
@ToString
public class PageParams {

    // 当前页码的默认值。
    public static final long DEFAULT_PAGE_CURRENT = 1L;
    // 每页记录数默认值。
    public static final long DEFAULT_PAGE_SIZE = 10L;

    // 当前页码。
    private Long pageNo = DEFAULT_PAGE_CURRENT;
    // 每页记录数默认值。
    private Long pageSize  = DEFAULT_PAGE_SIZE;

    public PageParams() {

    }

    public PageParams(Long pageNo, Long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}