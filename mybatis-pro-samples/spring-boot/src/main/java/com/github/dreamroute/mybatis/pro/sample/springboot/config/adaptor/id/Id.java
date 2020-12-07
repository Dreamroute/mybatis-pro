package com.github.dreamroute.mybatis.pro.sample.springboot.config.adaptor.id;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 前端请求中只只有1个id参数
 *
 * @author w.dehai
 */
@Data
public class Id {

    @NotNull
    @Min(value = Long.MIN_VALUE)
    @Max(value = Long.MAX_VALUE)
    private Long id;

}
