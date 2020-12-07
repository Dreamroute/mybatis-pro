package com.github.dreamroute.mybatis.pro.service.mapper;

import com.github.dreamroute.mybatis.pro.sdk.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author w.dehai
 */
public interface Mapper<T, ID> extends BaseMapper<T, ID> {

    /**
     * 逻辑删除实际上是物理删除，只是在删除的时候将数据迁移到backupTable(默认表名：backup_table)
     *
     * @param backupTable 备份表
     * @param tableName 被删除数据所在的表名
     * @param data 被删除数据JSON格式
     */
    @Insert("insert into ${backupTable} (`table_name`, `data`) values (#{tableName}, #{data})")
    void insertDynamic(@Param("backupTable") String backupTable, @Param("tableName") String tableName, @Param("data") String data);

}
