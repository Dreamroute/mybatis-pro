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
    @NotNull
    @Min(1)
    private Integer pageNum;

    @NotNull
    @Min(1)
    @Max(Long.MAX_VALUE)
    private Integer pageSize;

    /** 请求参数对象 **/
    @Valid
    private E params;

}
