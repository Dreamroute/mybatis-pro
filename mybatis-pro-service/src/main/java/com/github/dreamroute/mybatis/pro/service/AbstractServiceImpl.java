package com.github.dreamroute.mybatis.pro.service;

import com.alibaba.fastjson.JSON;
import com.github.dreamroute.mybatis.pro.core.annotations.Table;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author w.dehai
 */
@SuppressWarnings("ALL")
public class AbstractServiceImpl<T, ID> implements BaseService<T, ID> {

    @Autowired
    private Mapper<T, ID> mapper;

    private final Class<T> entityCls;

    public AbstractServiceImpl() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        entityCls = (Class<T>) typeArguments[0];
    }

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
        mapper.insertList(entityList);
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
        Table tableAnno = entityCls.getAnnotation(Table.class);
        String tableName = tableAnno.name();
        mapper.insertDynamic(tableName, data);
    }


}
