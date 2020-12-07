package com.github.dreamroute.mybatis.pro.sample.springboot.config.adaptor.id;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 前端请求中只有多个id参数
 *
 * @author w.dehai
 */
@Data
public class Ids {

    @NotEmpty
    private Long[] id;

    /**
     * 获取数组[1, 2, 3, ...]
     */
    public Long[] getIdsArray() {
        return this.id;
    }

    /**
     * 获取id值(1, 2, 3, ...)
     */
    public List<Long> getIdsList() {
        return Arrays.asList(id);
    }

    /**
     * 获取字符串"1, 2, 3, ..."
     */
    public String getIdsString() {
        return Arrays.stream(this.id).map(String::valueOf).collect(Collectors.joining(","));
    }

}
