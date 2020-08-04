package com.github.dreamroute.mybatis.pro.core.util;

import com.github.dream.mybatis.pro.sdk.Mapper;
import com.github.dreamroute.mybatis.pro.core.consts.MapperLabel;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Document;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * @author w.dehai
 */
public class MapperUtil {

    private Resource resource;
    private Set<Class<?>> parentInters;
    private Class<?> entityCls;
    private String entityClsStr;
    private String idCls;
    private String tableName;
    private String idName;

    // -- biz
    private String selectPrefix;
    private String insertPrefix;
    private String updatePrefix;
    private String deletePrefix;

    private String selectById;
    private String selectByIds;
    private String selectAll;

    private String insert;
    private String insertBySelect;

    private String update;
    private String updateBySelect;

    private String deleteById;
    private String deleteByIds;

    public MapperUtil(Resource resource) {
        this.resource = resource;
        Class<?> mapper = ResourceUtil.getMapperByResource(resource);
        parentInters = ClassUtil.getAllParentInterface(mapper);
        if (parentInters.contains(Mapper.class)) {
            Type[] genericInterfaces = mapper.getGenericInterfaces();
            ParameterizedType pt = (ParameterizedType) genericInterfaces[0];
            Type[] args = pt.getActualTypeArguments();
            entityClsStr = args[0].getTypeName();
            idCls = args[1].getTypeName();
            tableName = ClassUtil.getTableNameFromEntity(entityClsStr);
            try {
                entityCls = ClassUtils.forName(entityClsStr, getClass().getClassLoader());
                idName = ClassUtil.getIdName(entityCls);
            } catch (ClassNotFoundException e) {
                // ignore
            }
            
            processBiz();
        }
    }

    private void processBiz() {
        selectPrefix = "select * from " + tableName;
        insertPrefix = "insert into " + tableName;
        updatePrefix = "update " + tableName;
        deletePrefix = "delete from" + tableName;

        selectById = selectPrefix + " where " + idName + " = #{id}";
        selectByIds = selectPrefix + " where id in <foreach collection='list' item='id' index='index' open='(' close=')' separator=','>#{id}</foreach>";
        selectAll = selectPrefix;
    }

    public Resource parse() {
        Document doc = DocumentUtil.createDocumentFromResource(resource);
        return processSelectMapper(doc);
    }

    private Resource processSelectMapper(Document doc) {
        DocumentUtil.fillSqlNode(doc, MapperLabel.SELECT, "selectById", entityClsStr, selectById);
        return DocumentUtil.createResourceFromDocument(doc);
    }

}