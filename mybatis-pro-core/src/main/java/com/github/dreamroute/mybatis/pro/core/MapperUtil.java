package com.github.dreamroute.mybatis.pro.core;

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
    private String entityCls;
    private String idCls;
    private String tableName;
    private String idName;

    public MapperUtil(Resource resource) {
        this.resource = resource;
        Class<?> mapper = ResourceUtil.getMapperByResource(resource);
        parentInters = ClassUtil.getAllParentInterface(mapper);
        if (parentInters.contains(Mapper.class)) {
            Type[] genericInterfaces = mapper.getGenericInterfaces();
            ParameterizedType pt = (ParameterizedType) genericInterfaces[0];
            Type[] args = pt.getActualTypeArguments();
            entityCls = args[0].getTypeName();
            idCls = args[1].getTypeName();
            tableName = ResourceUtil.getTableName(entityCls);
            try {
                Class<?> entity = ClassUtils.forName(entityCls, getClass().getClassLoader());
                idName = ClassUtil.getIdName(entity);
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
    }

    public Resource parse() {
        Document doc = ResourceUtil.createDoc(resource);
        return processSelectMapper(doc);
    }

    private Resource processSelectMapper(Document doc) {
        String sql = "select * from " + tableName + " where " + idName + " = #{id}";
        DocumentUtil.createSelectMethod(doc, MapperLabel.SELECT.getCode(), "selectById", entityCls, sql);
        return DocumentUtil.createResourceFromDocument(doc);
    }

}