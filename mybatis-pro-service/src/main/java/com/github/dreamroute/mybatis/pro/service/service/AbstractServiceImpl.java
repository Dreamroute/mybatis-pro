package com.github.dreamroute.mybatis.pro.service.service;

import com.alibaba.fastjson.JSON;
import com.github.dreamroute.mybatis.pro.core.annotations.Table;
import com.github.dreamroute.mybatis.pro.core.consts.DriverType;
import com.github.dreamroute.mybatis.pro.service.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.util.List;

import static cn.hutool.core.annotation.AnnotationUtil.getAnnotationValue;
import static cn.hutool.core.util.ClassUtil.getTypeArgument;
import static com.github.dreamroute.mybatis.pro.core.consts.DriverType.SQLSERVER;
import static com.github.dreamroute.mybatis.pro.core.util.DriverUtil.getDriver;

/**
 * @author w.dehai
 */
public class AbstractServiceImpl<T, ID> implements BaseService<T, ID> {

    @Autowired
    private BaseMapper<T, ID> mapper;
    @Autowired
    private DataSource dataSource;

    @Value("${mybatis.pro.backup-table:backup_table}")
    private String backupTable;

    @Override
    public T insert(T entity) {
        mapper.insert(entity);
        return entity;
    }

    @Override
    public T insertExcludeNull(T entity) {
        mapper.insertExcludeNull(entity);
        return entity;
    }

    @Override
    public List<T> insertList(List<T> entityList) {
        DriverType driver = getDriver(dataSource);
        if (driver.equals(SQLSERVER)) {
            entityList.forEach(mapper::insert);
        } else {
            mapper.insertList(entityList);
        }
        return entityList;
    }

    @Override
    public int delete(ID id) {
        backup(id);
        return this.deleteDanger(id);
    }

    @Override
    public int deleteDanger(ID id) {
        return mapper.deleteById(id);
    }

    @Override
    public int delete(List<ID> ids) {
        ids.forEach(this::backup);
        return this.deleteDanger(ids);
    }

    @Override
    public int deleteDanger(List<ID> ids) {
        return mapper.deleteByIds(ids);
    }

    @Override
    public int update(T entity) {
        return mapper.updateById(entity);
    }

    @Override
    public int updateExcludeNull(T entity) {
        return mapper.updateByIdExcludeNull(entity);
    }

    @Override
    public T select(ID id) {
        return mapper.selectById(id);
    }

    @Override
    public List<T> select(List<ID> ids) {
        return mapper.selectByIds(ids);
    }

    @Override
    public List<T> selectAll() {
        return mapper.selectAll();
    }

    private void backup(ID id) {
        T entity = this.select(id);
        String data = JSON.toJSONString(entity);
        String tableName = getAnnotationValue(getTypeArgument(getClass()), Table.class);
        mapper.insertDynamic(backupTable, tableName, data);
    }

}
