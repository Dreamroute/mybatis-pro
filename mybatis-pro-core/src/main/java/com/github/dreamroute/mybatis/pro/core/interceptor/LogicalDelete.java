package com.github.dreamroute.mybatis.pro.core.interceptor;

import com.github.dreamroute.mybatis.pro.core.annotations.Table;
import lombok.Data;

import static com.github.dreamroute.mybatis.pro.core.consts.MyBatisProProperties.LOGICAL_DELETE_TABLE_NAME;

/**
 * 逻辑删除备份表
 */
@Data
@Table(LOGICAL_DELETE_TABLE_NAME)
public class LogicalDelete {
    private Long id;
    private String tableName;
    private String data;
}
