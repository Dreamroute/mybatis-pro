package com.github.dreamroute.mybatis.pro.sample.springboot.domain;

import com.github.dreamroute.mybatis.pro.core.annotations.Id;
import lombok.Data;

@Data
public class BaseDomain {
    @Id
    private Long id;
}
