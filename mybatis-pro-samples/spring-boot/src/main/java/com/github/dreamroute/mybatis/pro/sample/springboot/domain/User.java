package com.github.dreamroute.mybatis.pro.sample.springboot.domain;

import com.github.dreamroute.mybatis.pro.core.annotations.Column;
import com.github.dreamroute.mybatis.pro.core.annotations.Table;
import com.github.dreamroute.mybatis.pro.core.annotations.Transient;
import com.github.dreamroute.mybatis.pro.service.entity.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table("smart_user")
public class User extends IdEntity {

    private String name;
    private String password;
    private Long version;
    @Transient
    private Integer gender;
    @Column("phone_no")
    private String phoneNo;

    public void setId(Long id) {
        super.setId(id);
    }
}
