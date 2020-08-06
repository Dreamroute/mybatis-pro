package com.github.dreamroute.mybatis.pro.core.util;

import com.github.dream.mybatis.pro.sdk.Mapper;
import com.github.dreamroute.mybatis.pro.core.annotations.Column;
import com.github.dreamroute.mybatis.pro.core.annotations.Id;
import com.github.dreamroute.mybatis.pro.core.annotations.Type;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author w.dehai
 */
public class MapperUtil {

    private final Document document;
    private Class<?> entityCls;
    private String entityClsStr;
    private String tableName;
    private String idColumn;
    private String idName;
    private com.github.dreamroute.mybatis.pro.core.annotations.Type type;

    // -- biz
    private String insertPrefix;
    private String updateByIdPrefix;

    private Map<String, String> methodName2Sql = new HashMap<>();

    private String commonWhereIdIs = null;

    final String trimStart = "<trim suffixOverrides=','>";
    final String trimEnd = "</trim>";

    String insertColumns;
    String insertValues;
    String insertExcludeNullColumns;
    String insertExcludeNullValues;
    String updateByIdColumns;
    String updateByIdExcludeNullColumns;

    public MapperUtil(Resource resource) {
        this.document = DocumentUtil.createDocumentFromResource(resource);
        Class<?> mapper = ResourceUtil.getMapperByResource(resource);
        Set<Class<?>> parentInters = ClassUtil.getAllParentInterface(mapper);
        if (parentInters.contains(Mapper.class)) {
            this.entityClsStr = ClassUtil.getMapperGeneric(mapper);
            this.tableName = ClassUtil.getTableNameFromEntity(entityClsStr);
            try {
                this.entityCls = ClassUtils.forName(entityClsStr, getClass().getClassLoader());
                this.idColumn = ClassUtil.getIdColumn(entityCls);
                this.idName = ClassUtil.getIdName(entityCls);
                this.createSqlFragment();
            } catch (ClassNotFoundException e) {
                // ignore
            }

            processBiz();
        }
    }

    private void processBiz() {
        String selectPrefix = "select * from " + tableName;
        String deletePrefix = "delete from " + tableName;

        insertPrefix = "INSERT INTO " + tableName.toLowerCase();
        updateByIdPrefix = "update " + tableName;

        commonWhereIdIs = " where " + idColumn + " = #{" + idName + "}";
        String commonWhereIdIn = " where " + idColumn + " in <foreach collection='list' item='id' index='index' open='(' close=')' separator=','>#{" + idName + "}</foreach>";

        String selectById = selectPrefix + commonWhereIdIs;
        String selectByIds = selectPrefix + commonWhereIdIn;
        methodName2Sql.put("selectById", selectById);
        methodName2Sql.put("selectByIds", selectByIds);
        methodName2Sql.put("selectAll", selectPrefix);

        String insert = insertPrefix + " (" + this.insertColumns + "( VALUE (" + this.insertValues + ")";
        String insertList = insertPrefix + " " + this.insertColumns + " VALUES <foreach collection='list' item='item' index='index' separator=','>" + this.insertValues.replace("#{", "#{item.") + "</foreach>";
        String insertExcludeNull = insertPrefix + " (" + this.insertExcludeNullColumns + ") VALUE (" + this.insertExcludeNullValues + ")";
        methodName2Sql.put("insert", insert);
        methodName2Sql.put("insertList", insertList);
        methodName2Sql.put("insertExcludeNull", insertExcludeNull);

        String updateById = updateByIdPrefix + " set " + this.updateByIdColumns + commonWhereIdIs;
        String updateByIdExcludeNull = updateByIdPrefix + " set " + this.updateByIdExcludeNullColumns + " where " + this.idColumn + " = #{" + this.idName + "}";
        methodName2Sql.put("updateById", updateById);
        methodName2Sql.put("updateByIdExcludeNull", updateByIdExcludeNull);

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

                    DocumentUtil.fillSqlNode(this.document, ml, methodName, returnType, methodName2Sql.get(methodName), type, idName);
                });
        return DocumentUtil.createResourceFromDocument(this.document);
    }

    private void createSqlFragment() {
        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();
        Map<String, String> values2Columns = new HashMap<>();
        IdType idType = new IdType();
        PrimaryKey pk = new PrimaryKey();

        ReflectionUtils.doWithFields(entityCls, field -> {
            Column colAn = field.getAnnotation(Column.class);
            String column = Optional.ofNullable(colAn).map(Column::name).orElse(SqlUtil.toLine(field.getName()));
            values2Columns.put(field.getName(), column);

            Id idAn = field.getAnnotation(Id.class);
            if (idAn != null) {
                idType.type = idAn.type();
                pk.name = field.getName();
            }
        }, ClassUtil::isBeanProp);

        if (idType.type == Type.IDENTITY) {
            values2Columns.remove(pk.name);
        }
        values2Columns.forEach((fieldName, column) -> {
            columns.add(column);
            values.add(fieldName);
        });

        this.insertColumns = columns.stream().map(column -> "`" + column + "`").collect(Collectors.joining(",", "(", ")"));
        this.insertValues = values.stream().map(column -> "#{" + column + "}").collect(Collectors.joining(",", "(", ")"));
        this.createInsertExcludeNullColumnsAndValues(columns, values);
        this.createUpdateByIdColumns(columns, values);
        this.createUpdateByIdExcludeNullColumns(columns, values);
    }

    private static class IdType {
        Type type;
    }

    private static class PrimaryKey {
        String name;
    }

    private void createInsertExcludeNullColumnsAndValues(List<String> columns, List<String> values) {
        StringBuilder insertExcludeNullColumns = new StringBuilder();
        StringBuilder insertExcludeNullValues = new StringBuilder();
        for (int i=0; i<columns.size(); i++) {
            insertExcludeNullColumns.append("<if test = '" + values.get(i) + " != null'>" + columns.get(i) + ",</if>");
            insertExcludeNullValues.append("<if test = '" + values.get(i) + " != null'>#{" + values.get(i) + "},</if>");
        }

        this.insertExcludeNullColumns = trimStart + insertExcludeNullColumns.toString() + trimEnd;
        this.insertExcludeNullValues = trimStart + insertExcludeNullValues.toString() + trimEnd;
    }

    private void createUpdateByIdColumns(List<String> columns, List<String> values) {
        StringBuilder result = new StringBuilder();
        for (int i=0; i<columns.size(); i++) {
            result.append("`").append(columns.get(i)).append("` = #{").append(values.get(i)).append("}");
            if (i != columns.size() - 1) {
                result.append(",");
            }
        }
        this.updateByIdColumns = result.toString();
    }

    private void createUpdateByIdExcludeNullColumns(List<String> columns, List<String> values) {
        StringBuilder result = new StringBuilder();
        for (int i=0; i<columns.size(); i++) {
            result.append("<if test = '" + values.get(i) + " != null'>`" + columns.get(i) + "` = #{" + values.get(i) + "},</if>");
        }
        this.updateByIdExcludeNullColumns = trimStart + result.toString() + trimEnd;
    }
}

