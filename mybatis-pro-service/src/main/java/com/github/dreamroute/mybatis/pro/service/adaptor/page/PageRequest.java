package com.github.dreamroute.mybatis.pro.service.adaptor.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 分页请求
 *
 * @author w.dehai
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class PageRequest<E> {

    /** 分页信息 **/
    @Min(1)
    @NotNull
    @Builder.Default
    @Max(Integer.MAX_VALUE)
    private int pageNum = 1;

    @Min(1)
    @NotNull
    @Builder.Default
    @Max(Integer.MAX_VALUE)
    private int pageSize = 10;

    /** 请求参数对象 **/
    @Valid
    private E param;

}
