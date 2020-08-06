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
    private String tableName;
    private String idColumn;
    private String idName;
    private SqlFragment sqlFragment;
    private com.github.dreamroute.mybatis.pro.core.annotations.Type type;

    // -- biz
    private String selectPrefix;
    private String insertPrefix;
    private String updateByIdPrefix;
    private String deletePrefix;

    private Map<String, String> methodName2Sql = new HashMap<>();

    private String commonWhereIdIs = null;
    private String commonWhereIdIn = null;

    public MapperUtil(Resource resource) {
        this.document = DocumentUtil.createDocumentFromResource(resource);
        Class<?> mapper = ResourceUtil.getMapperByResource(resource);
        this.parentInters = ClassUtil.getAllParentInterface(mapper);
        if (parentInters.contains(Mapper.class)) {
            this.entityClsStr = ClassUtil.getMapperGeneric(mapper);
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
        updateByIdPrefix = "update " + tableName;
        deletePrefix = "delete from " + tableName;

        commonWhereIdIs = " where " + idColumn + " = #{" + idName + "}";
        commonWhereIdIn = " where " + idColumn + " in <foreach collection='list' item='id' index='index' open='(' close=')' separator=','>#{" + idName + "}</foreach>";

        String selectById = selectPrefix + commonWhereIdIs;
        String selectByIds = selectPrefix + commonWhereIdIn;
        String selectAll = selectPrefix;
        methodName2Sql.put("selectById", selectById);
        methodName2Sql.put("selectByIds", selectByIds);
        methodName2Sql.put("selectAll", selectAll);

        String insert = createInsert();
        String insertList = createInsertList();
        methodName2Sql.put("insert", insert);
        methodName2Sql.put("insertList", insertList);
//
        String updateById = createUpdateById();
        methodName2Sql.put("updateById", updateById);
        String deleteById = deletePrefix + commonWhereIdIs;
        String deleteByIds = deletePrefix + commonWhereIdIn;
        methodName2Sql.put("deleteById", deleteById);
        methodName2Sql.put("deleteByIds", deleteByIds);

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
        return insertPrefix + " " + sqlFragment.insertColumns.toUpperCase() + " VALUE " + sqlFragment.insertValues;
    }

    private String createInsertList() {
        return
                insertPrefix + " " + sqlFragment.insertColumns.toUpperCase() + " VALUES " + "" +
                        "<foreach collection='list' item='item' index='index' separator=','>" +
                        sqlFragment.insertValues.replace("#{", "#{item.") +
                        "</foreach>";
    }

    private String createUpdateById() {
        return updateByIdPrefix + " set " + this.sqlFragment.updateByIdNamesAndValues + commonWhereIdIs;
    }

    private SqlFragment createSqlFragment() {
        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();
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
                if (id != null) {
                    columns.add(id);
                    values.add(field.getName());
                }
            } else {
                String column = SqlUtil.toLine(field.getName());
                Column colAn = field.getAnnotation(Column.class);
                if (colAn != null) {
                    column = colAn.name();
                }
                columns.add(column);
                values.add(field.getName());
            }
        }, field -> !ClassUtil.specialProp(field));
        String insertColumns = columns.stream().map(column -> "`" + column + "`").collect(Collectors.joining(",", "(", ")"));
        String insertValues = values.stream().map(column -> "#{" + column + "}").collect(Collectors.joining(",", "(", ")"));
        String updateByIdNamesAndValues = createUpdateByIdNamesAndValues(columns, values);

        SqlFragment fragment = new SqlFragment();
        fragment.insertColumns = insertColumns;
        fragment.insertValues = insertValues;
        fragment.updateByIdNamesAndValues = updateByIdNamesAndValues;
        return fragment;
    }

    private static class SqlFragment {
        String insertColumns;
        String insertValues;
        String updateByIdNamesAndValues;
    }

    private String createUpdateByIdNamesAndValues(List<String> columns, List<String> values) {
        StringBuilder result = new StringBuilder();
        for (int i=0; i<columns.size(); i++) {
            result.append("`").append(columns.get(i)).append("` = #{").append(values.get(i)).append("}");
            if (i != columns.size() - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }
}