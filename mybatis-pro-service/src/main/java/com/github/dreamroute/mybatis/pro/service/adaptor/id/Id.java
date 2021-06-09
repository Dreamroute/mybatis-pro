package com.github.dreamroute.mybatis.pro.service.adaptor.id;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 前端请求中只只有1个id参数
 *
 * @author w.dehai
 */
@Data
public class Id implements Serializable {

    @NotNull
    @Min(value = Long.MIN_VALUE)
    @Max(value = Long.MAX_VALUE)
    private Long id;

}
