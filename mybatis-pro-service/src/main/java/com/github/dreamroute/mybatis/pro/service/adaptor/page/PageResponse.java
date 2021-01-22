package com.github.dreamroute.mybatis.pro.service.adaptor.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果
 *
 * @author w.dehai
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

    private int pageNum;
    private int pageSize;
    private long totalNum;
    private List<T> data;

//    public PageResponse(Page<T> page) {
//        this.pageNum = page.getPageNum();
//        this.pageSize = page.getPageSize();
//        this.totalNum = page.getTotal();
//        this.data = page;
//    }

}
