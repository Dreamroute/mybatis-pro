package com.github.dreamroute.mybatis.pro.service.service;

import com.github.dreamroute.mybatis.pro.service.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author w.dehai
 */
public class AbstractServiceImpl<T, ID> implements BaseService<T, ID> {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private BaseMapper<T, ID> mapper;

    @Override
    @Transactional
    public T insert(T entity) {
        mapper.insert(entity);
        return entity;
    }

    @Override
    @Transactional
    public T insertExcludeNull(T entity) {
        mapper.insertExcludeNull(entity);
        return entity;
    }

    @Override
    @Transactional
    public List<T> insertList(List<T> entityList) {
        mapper.insertList(entityList);
        return entityList;
    }

    @Override
    @Transactional
    public int delete(ID id) {
        return mapper.deleteById(id);
    }

    @Override
    @Transactional
    public int delete(List<ID> ids) {
        return mapper.deleteByIds(ids);
    }

    @Override
    @Transactional
    public int update(T entity) {
        return mapper.updateById(entity);
    }

    @Override
    @Transactional
    public int updateExcludeNull(T entity) {
        return mapper.updateByIdExcludeNull(entity);
    }

    @Override
    public T selectById(ID id, String... cols) {
        return mapper.selectById(id, cols);
    }

    @Override
    public List<T> selectByIds(List<ID> ids, String... cols) {
        return mapper.selectByIds(ids, cols);
    }

    @Override
    public List<T> selectAll(String... cols) {
        return mapper.selectAll(cols);
    }

}
