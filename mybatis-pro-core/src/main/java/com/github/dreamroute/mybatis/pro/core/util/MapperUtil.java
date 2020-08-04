package com.github.dreamroute.mybatis.pro.core.util;

import com.github.dream.mybatis.pro.sdk.Mapper;
import com.github.dreamroute.mybatis.pro.core.consts.MapperLabel;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Document;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author w.dehai
 */
public class MapperUtil {

    private Document document;
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

    private String insert;
    private String insertBySelect;

    private String update;
    private String updateBySelect;

    private String deleteById;
    private String deleteByIds;

    private Map<String, String> methodName2Sql = new HashMap<>();

    public MapperUtil(Resource resource) {
        this.document = DocumentUtil.createDocumentFromResource(resource);
        Class<?> mapper = ResourceUtil.getMapperByResource(resource);
        this.parentInters = ClassUtil.getAllParentInterface(mapper);
        if (parentInters.contains(Mapper.class)) {
            Type[] genericInterfaces = mapper.getGenericInterfaces();
            ParameterizedType pt = (ParameterizedType) genericInterfaces[0];
            Type[] args = pt.getActualTypeArguments();
            this.entityClsStr = args[0].getTypeName();
            this.idCls = args[1].getTypeName();
            this.tableName = ClassUtil.getTableNameFromEntity(entityClsStr);
            try {
                this.entityCls = ClassUtils.forName(entityClsStr, getClass().getClassLoader());
                this.idName = ClassUtil.getIdName(entityCls);
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

        String selectById = selectPrefix + " where " + idName + " = #{id}";
        String selectByIds = selectPrefix + " where id in <foreach collection='list' item='id' index='index' open='(' close=')' separator=','>#{id}</foreach>";
        String selectAll = selectPrefix;
        methodName2Sql.put("selectById", selectById);
        methodName2Sql.put("selectByIds", selectByIds);
        methodName2Sql.put("selectAll", selectAll);

        String insert = insertPrefix + "() values ()";
        String insertBySelect = insertPrefix + "() values ()";
        String insertList = "";
        String insertListBySelect = "";
        methodName2Sql.put("insert", insert);
        methodName2Sql.put("insertBySelect", insertBySelect);
        methodName2Sql.put("insertList", insertList);
        methodName2Sql.put("insertListBySelect", insertListBySelect);

        String update = updatePrefix + "";
        String updateBySelect = "";
        methodName2Sql.put("update", update);
        methodName2Sql.put("updateBySelect", updateBySelect);

        String deleteById = "";
        String deleteByIds = "";
        methodName2Sql.put("deleteById", deleteById);
        methodName2Sql.put("deleteByIds", deleteByIds);

    }

    public Resource parse() {
        Method[] methods = Mapper.class.getMethods();
        String name = methods[0].getName();
        System.err.println(name);
        Stream.of(methods)
                .map(Method::getName)
                .filter(methodName -> methodName.startsWith(MapperLabel.SELECT.getCode()))
                .forEach(selectName -> DocumentUtil.fillSqlNode(document, MapperLabel.SELECT, selectName, entityClsStr, methodName2Sql.get(selectName)));
        return DocumentUtil.createResourceFromDocument(document);
    }

}