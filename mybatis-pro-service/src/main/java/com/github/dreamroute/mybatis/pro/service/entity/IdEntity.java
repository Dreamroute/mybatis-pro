package com.github.dreamroute.mybatis.pro.service.entity;

import com.github.dreamroute.mybatis.pro.core.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class IdEntity {
    @Id
    private Long id;
}
