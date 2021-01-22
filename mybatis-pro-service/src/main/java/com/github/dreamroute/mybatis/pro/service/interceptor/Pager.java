package com.github.dreamroute.mybatis.pro.service.interceptor;

import com.github.dreamroute.mybatis.pro.service.adaptor.page.PageRequest;
import com.github.dreamroute.mybatis.pro.service.adaptor.page.PageResponse;

import java.util.List;
import java.util.function.Function;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class Pager {
    public static <T, R> PageResponse<R> page(PageRequest<T> pageRequest, Function<PageRequest<T>, PageContainer<R>> query) {
        PageContainer<R> resp = query.apply(pageRequest);
        PageResponse<R> result = new PageResponse<>();
        List<R> collect = ofNullable(resp).orElseGet(PageContainer::new).stream().collect(toList());
        result.setTotalNum(resp.getTotal());
        result.setData(collect);
        result.setPageNum(resp.getPageNum());
        result.setPageSize(resp.getPageSize());
        return result;
    }
}
