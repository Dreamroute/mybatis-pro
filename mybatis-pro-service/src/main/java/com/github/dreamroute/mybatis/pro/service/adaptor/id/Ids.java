package com.github.dreamroute.mybatis.pro.service.adaptor.id;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 前端请求中只有多个id参数
 *
 * @author w.dehai
 */
@Data
public class Ids {

    @NotEmpty
    private Long[] ids;

}
