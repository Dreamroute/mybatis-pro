package com.github.dreamroute.mybatis.pro.sample.springboot.domain;

import com.github.dreamroute.mybatis.pro.core.annotations.Table;
import com.github.dreamroute.mybatis.pro.core.annotations.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "smart_user")
public class User extends BaseDomain implements Serializable {

    private static final long serialVersionUID = -4418452798454877258L;

    private String name;
    private String password;
    private Long version;
    @Transient
    private Integer gender;
    private String phoneNo;
}
