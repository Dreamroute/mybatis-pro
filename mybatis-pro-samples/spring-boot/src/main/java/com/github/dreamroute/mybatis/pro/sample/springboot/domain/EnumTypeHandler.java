package com.github.dreamroute.mybatis.pro.sample.springboot.domain;

import com.github.dreamroute.mybatis.pro.core.annotations.Id;
import com.github.dreamroute.mybatis.pro.core.annotations.Table;
import lombok.Data;

@Data
@Table("smart_typehandler")
public class EnumTypeHandler {
    @Id
    private Long id;
    private Gender gender;
}
