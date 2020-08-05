package com.github.dreamroute.mybatis.pro.core.util;

import com.github.dream.mybatis.pro.sdk.Mapper;
import com.github.dreamroute.mybatis.pro.core.annotations.Column;
import com.github.dreamroute.mybatis.pro.core.annotations.Id;
import com.github.dreamroute.mybatis.pro.core.consts.MapperLabel;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.w3c.dom.Document;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
    private String idColumn;
    private String idName;
    private SqlFragment sqlFragment;
    private com.github.dreamroute.mybatis.pro.core.annotations.Type type;

    // -- biz
    private String selectPrefix;
    private String insertPrefix;
    private String updatePrefix;
    private String deletePrefix;

    private String insert;
    private String insertBySelect;

    private String updateById;

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
                this.idColumn = ClassUtil.getIdColumn(entityCls);
                this.idName = ClassUtil.getIdName(entityCls);
                this.sqlFragment = createSqlFragment();
            } catch (ClassNotFoundException e) {
                // ignore
            }

            processBiz();
        }
    }

    private void processBiz() {
        selectPrefix = "select * from " + tableName;
        insertPrefix = "INSERT INTO " + tableName.toLowerCase();
        updatePrefix = "update " + tableName;
        deletePrefix = "delete from" + tableName;

        String selectById = selectPrefix + " where " + idColumn + " = #{id}";
        String selectByIds = selectPrefix + " where id in <foreach collection='list' item='id' index='index' open='(' close=')' separator=','>#{id}</foreach>";
        String selectAll = selectPrefix;
        methodName2Sql.put("selectById", selectById);
        methodName2Sql.put("selectByIds", selectByIds);
        methodName2Sql.put("selectAll", selectAll);

        String insert = createInsert();
        String insertList = createInsertList();
//        String insertBySelect = insertPrefix + "() values ()";
//        String insertListBySelect = "";
        methodName2Sql.put("insert", insert);
//        methodName2Sql.put("insertBySelect", insertBySelect);
        methodName2Sql.put("insertList", insertList);
//        methodName2Sql.put("insertListBySelect", insertListBySelect);
//
//        String update = "";
//        String updateBySelect = "";
//        methodName2Sql.put("update", update);
//        methodName2Sql.put("updateBySelect", updateBySelect);
//
//        String deleteById = "";
//        String deleteByIds = "";
//        methodName2Sql.put("deleteById", deleteById);
//        methodName2Sql.put("deleteByIds", deleteByIds);

    }

    /**
     * 将通用crud方法填充到resource里
     */
    public Resource parse() {
        Method[] methods = Mapper.class.getMethods();
        Stream.of(methods)
                .map(Method::getName)
                .forEach(methodName -> {
                    MapperLabel ml;
                    String returnType = null;
                    if (methodName.startsWith(MapperLabel.SELECT.getCode())) {
                        ml = MapperLabel.SELECT;
                        returnType = entityClsStr;
                    } else if (methodName.startsWith(MapperLabel.INSERT.getCode())) {
                        ml = MapperLabel.INSERT;
                    } else if (methodName.startsWith(MapperLabel.UPDATE.getCode())) {
                        ml = MapperLabel.UPDATE;
                    } else {
                        ml = MapperLabel.DELETE;
                    }

                    DocumentUtil.fillSqlNode(document, ml, methodName, returnType, methodName2Sql.get(methodName), type, idName);
                });
        return DocumentUtil.createResourceFromDocument(document);
    }

    private String createInsert() {
        return insertPrefix + " " + sqlFragment.names.toUpperCase() + " VALUE " + sqlFragment.values;
    }

    private String createInsertList() {
        return
                insertPrefix + " " + sqlFragment.names.toUpperCase() + " VALUES " + "" +
                        "<foreach collection='list' item='item' index='index' separator=','>" +
                        sqlFragment.values.replace("#{", "#{item.") +
                        "</foreach>";
    }

    private String createUpdate() {
        return null;
    }

    private SqlFragment createSqlFragment() {
        List<String> columns = new ArrayList<>();
        ReflectionUtils.doWithFields(entityCls, field -> {
            Id idAn = field.getAnnotation(Id.class);
            if (idAn != null) {
                String id = null;
                if (idAn.type() == com.github.dreamroute.mybatis.pro.core.annotations.Type.AUTO) {
                    id = SqlUtil.toLine(field.getName());
                    Column colAn = field.getAnnotation(Column.class);
                    if (colAn != null) {
                        id = colAn.name();
                    }
                } else {
                    type = com.github.dreamroute.mybatis.pro.core.annotations.Type.IDENTITY;
                }
                if (id != null)
                    columns.add(id);
            } else {
                String column = SqlUtil.toLine(field.getName());
                Column colAn = field.getAnnotation(Column.class);
                if (colAn != null) {
                    column = colAn.name();
                }
                columns.add(column);
            }
        }, field -> !ClassUtil.specialProp(field));
        String names = columns.stream().map(column -> "`" + column + "`").collect(Collectors.joining(",", "(", ")"));
        String values = columns.stream().map(column -> "#{" + column + "}").collect(Collectors.joining(",", "(", ")"));
        SqlFragment fragment = new SqlFragment();
        fragment.names = names;
        fragment.values = values;
        return fragment;
    }

    private static class SqlFragment {
        String names;
        String values;
    }
}