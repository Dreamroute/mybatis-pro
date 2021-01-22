package com.github.dreamroute.mybatis.pro.service.interceptor;

import lombok.Data;

import java.util.ArrayList;

@Data
public class PageContainer<E> extends ArrayList<E> {
    private long total;
    private int pageNum;
    private int pageSize;
}
