package com.github.dreamroute.mybatis.pro.core.util;

import com.github.dreamroute.mybatis.pro.core.annotations.Column;
import com.github.dreamroute.mybatis.pro.core.annotations.Id;
import com.github.dreamroute.mybatis.pro.core.annotations.Table;
import com.github.dreamroute.mybatis.pro.core.annotations.Type;
import com.github.dreamroute.mybatis.pro.core.consts.MapperLabel;
import com.github.dreamroute.mybatis.pro.sdk.BaseMapper;
import org.springframework.core.io.Resource;
import org.springframework.util.ReflectionUtils;
import org.w3c.dom.Document;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.annotation.AnnotationUtil.getAnnotationValue;
import static cn.hutool.core.util.ClassUtil.getTypeArgument;
import static com.github.dreamroute.mybatis.pro.core.util.ClassUtil.getAllParentInterface;
import static com.github.dreamroute.mybatis.pro.core.util.ClassUtil.getIdField;
import static com.github.dreamroute.mybatis.pro.core.util.DocumentUtil.createDocumentFromResource;
import static com.github.dreamroute.mybatis.pro.core.util.MyBatisProUtil.getMapperByResource;
import static com.github.dreamroute.mybatis.pro.core.util.SqlUtil.toLine;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author w.dehai
 */
public class MapperUtil {

    private final Document document;
    private Class<?> entityCls;
    private String tableName;
    private String idColumn;
    private String idName;
    private Class<?> mapper;
    private com.github.dreamroute.mybatis.pro.core.annotations.Type type;

    // -- biz
    private final Map<String, String> methodName2Sql = new HashMap<>();

    private static final String TRIM_START = "<trim suffixOverrides=','>";
    private static final String TRIM_END = "</trim>";
    private static final String WHERE = " where ";

    String insertColumns;
    String insertValues;
    String insertExcludeNullColumns;
    String insertExcludeNullValues;
    String updateByIdColumns;
    String updateByIdExcludeNullColumns;

    public MapperUtil(Resource resource) {
        this.document = createDocumentFromResource(resource);
        mapper = getMapperByResource(resource);
        Set<Class<?>> parentInters = getAllParentInterface(mapper);
        if (parentInters.contains(BaseMapper.class)) {
            this.entityCls = getTypeArgument(mapper);
            this.tableName = getAnnotationValue(entityCls, Table.class);
            Field idField = getIdField(entityCls);
            this.idName = idField.getName();
            String col = getAnnotationValue(idField, Column.class);
            this.idColumn = isEmpty(col) ? toLine(idField.getName()) : col;
            this.type = idField.getAnnotation(Id.class).type();
            this.createSqlFragment();

            processBiz();
        }
    }

    private void processBiz() {
        String selectPrefix = "select * from " + tableName;
        String deletePrefix = "delete from " + tableName;

        String insertPrefix = "insert into " + tableName;
        String updateByIdPrefix = "update " + tableName;

        String commonWhereIdIs = WHERE + idColumn + " = #{" + idName + "}";
        String commonWhereIdIn = WHERE + idColumn + " in <foreach collection='list' item='id' index='index' open='(' close=')' separator=','>#{" + idName + "}</foreach>";

        String selectById = selectPrefix + commonWhereIdIs;
        String selectByIds = selectPrefix + commonWhereIdIn;
        methodName2Sql.put("selectById", selectById);
        methodName2Sql.put("selectByIds", selectByIds);
        methodName2Sql.put("selectAll", selectPrefix);

        String insert = insertPrefix + this.insertColumns + " VALUES " + this.insertValues;
        String insertList = insertPrefix + " " + this.insertColumns + " VALUES <foreach collection='list' item='item' index='index' separator=','>" + this.insertValues.replace("#{", "#{item.") + "</foreach>";
        String insertExcludeNull = insertPrefix + " (" + this.insertExcludeNullColumns + ") VALUES (" + this.insertExcludeNullValues + ")";
        methodName2Sql.put("insert", insert);
        methodName2Sql.put("insertList", insertList);
        methodName2Sql.put("insertExcludeNull", insertExcludeNull);

        String updateById = updateByIdPrefix + " set " + this.updateByIdColumns + commonWhereIdIs;
        String updateByIdExcludeNull = updateByIdPrefix + " set " + this.updateByIdExcludeNullColumns + WHERE + this.idColumn + " = #{" + this.idName + "}";
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
        Method[] methods = BaseMapper.class.getMethods();
        Stream.of(methods)
                .map(Method::getName)
                .forEach(methodName -> {
                    MapperLabel ml;
                    String returnType = null;
                    if (methodName.startsWith(MapperLabel.SELECT.getCode())) {
                        ml = MapperLabel.SELECT;
                        returnType = entityCls.getName();
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
        Map<String, String> values2Columns = new HashMap<>();
        IdType pkType = new IdType();
        PrimaryKey pk = new PrimaryKey();

        ReflectionUtils.doWithFields(entityCls, field -> {
            Column colAn = field.getAnnotation(Column.class);
            String column = Optional.ofNullable(colAn).map(Column::value).orElse(SqlUtil.toLine(field.getName()));
            values2Columns.put(field.getName(), column);

            Id idAn = field.getAnnotation(Id.class);
            if (idAn != null) {
                pkType.type = idAn.type();
                pk.name = field.getName();
            }
        }, ClassUtil::isBeanProp);

        if (pkType.type == Type.IDENTITY) {
            values2Columns.remove(pk.name);
        }
        List<String> columns = new ArrayList<>();
        List<String> values = new ArrayList<>();
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
        StringBuilder insertExcludeNullCols = new StringBuilder();
        StringBuilder insertExcludeNullVals = new StringBuilder();
        for (int i=0; i<columns.size(); i++) {
            insertExcludeNullCols.append("<if test = '" + values.get(i) + " != null'>`" + columns.get(i) + "`,</if>");
            insertExcludeNullVals.append("<if test = '" + values.get(i) + " != null'>#{" + values.get(i) + "},</if>");
        }

        this.insertExcludeNullColumns = TRIM_START + insertExcludeNullCols.toString() + TRIM_END;
        this.insertExcludeNullValues = TRIM_START + insertExcludeNullVals.toString() + TRIM_END;
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
        this.updateByIdExcludeNullColumns = TRIM_START + result.toString() + TRIM_END;
    }
}

