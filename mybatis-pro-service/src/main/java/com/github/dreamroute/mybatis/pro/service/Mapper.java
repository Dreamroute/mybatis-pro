package com.github.dreamroute.mybatis.pro.service;

import com.github.dreamroute.mybatis.pro.sdk.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author w.dehai
 */
public interface Mapper<T, ID> extends BaseMapper<T, ID> {

    /**
     * 动态增加逻辑删除数据
     */
    @Insert("insert into backup_table (`table_name`, `data`) values (#{tableName}, #{data})")
    void insertDynamic(@Param("tableName") String tableName, @Param("data") String data);

}
