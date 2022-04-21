package com.githu.dreamroute.mybatis.pro.base;

import lombok.Data;

import java.util.Date;

/**
 * @author w.dehai.2021/8/10.14:50
 */
@Data
public class User {
    private Long id;
    private Gender gender;
    private Date birthday;
}
