package com.github.dreamroute.mybatis.pro.sample.springboot.domain;

import com.github.dreamroute.mybatis.pro.core.annotations.Id;
import com.github.dreamroute.mybatis.pro.core.annotations.Table;
import com.github.dreamroute.mybatis.pro.core.annotations.Transient;
import lombok.Data;

@Data
@Table(name = "smart_user")
public class User {

    private static final long serialVersionUID = -8522466522078749737L;

    @Id
    private Long userId;
    private String name;
    private String password;
    private Long version;
    @Transient
    private Integer gender;
}
