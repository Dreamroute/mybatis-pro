package com.github.dreamroute.mybatis.pro.core.util;

import com.github.dreamroute.mybatis.pro.core.annotations.Table;
import com.github.dreamroute.mybatis.pro.core.consts.MapperLabel;
import com.github.dreamroute.mybatis.pro.core.exception.MyBatisProException;
import com.github.dreamroute.mybatis.pro.sdk.Mapper;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.hutool.core.annotation.AnnotationUtil.getAnnotationValue;
import static cn.hutool.core.annotation.AnnotationUtil.hasAnnotation;
import static cn.hutool.core.util.ClassUtil.getTypeArgument;
import static cn.hutool.core.util.ClassUtil.loadClass;
import static cn.hutool.core.util.ClassUtil.scanPackageBySuper;
import static com.alibaba.fastjson.JSON.toJSONString;
import static com.github.dreamroute.mybatis.pro.core.consts.MapperLabel.DELETE;
import static com.github.dreamroute.mybatis.pro.core.consts.MapperLabel.ID;
import static com.github.dreamroute.mybatis.pro.core.consts.MapperLabel.MAPPER;
import static com.github.dreamroute.mybatis.pro.core.consts.MapperLabel.NAMESPACE;
import static com.github.dreamroute.mybatis.pro.core.consts.MapperLabel.SELECT;
import static com.github.dreamroute.mybatis.pro.core.util.ClassUtil.getInnerMethodNames;
import static com.github.dreamroute.mybatis.pro.core.util.ClassUtil.getMethodName2ReturnType;
import static com.github.dreamroute.mybatis.pro.core.util.ClassUtil.getSpecialMethods;
import static com.github.dreamroute.mybatis.pro.core.util.DocumentUtil.createDocumentFromResource;
import static com.github.dreamroute.mybatis.pro.core.util.DocumentUtil.createResourceFromDocument;
import static com.github.dreamroute.mybatis.pro.core.util.DocumentUtil.fillSqlNode;
import static com.google.common.collect.Sets.difference;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * 使用新的resource替换默认resource，如果Mapper接口不存在对应的mapepr.xml文件，就创建接口Mapper对应的mapper.xml
 *
 * @author w.dehai
 */
public class MyBatisProUtil {

    private MyBatisProUtil() {}

    public static Resource[] processMyBatisPro(Resource[] xmlResources, Set<String> mapperPackages) {

        Set<Class<?>> mappers = ofNullable(mapperPackages).orElseGet(HashSet::new).stream().map(mapperPkgName -> scanPackageBySuper(mapperPkgName, Mapper.class)).flatMap(Set::stream).collect(toSet());
        Set<Class<?>> existXmlMapper = stream(ofNullable(xmlResources).orElse(new Resource[0])).map(MyBatisProUtil::getNamespaceFromXmlResource).collect(toSet());
        Set<Class<?>> extra = difference(mappers, existXmlMapper);

        Set<Resource> allResources = new HashSet<>();
        Set<Resource> extraResource = extra.stream().map(MyBatisProUtil::createEmptyResource).collect(toSet());
        allResources.addAll(extraResource);
        allResources.addAll(asList(ofNullable(xmlResources).orElseGet(() -> new Resource[0])));

        // 处理findBy, deleteBy, countBy, existBy方法
        Set<Resource> all = processSpecialMethods(allResources);

        // 处理通用crud
        Set<Resource> result = processMapperMethods(all);

        return result.toArray(new Resource[0]);
    }

    /**
     * 从mapper.xml文件中获取namespace
     *
     * @param resource mapper.xml数据流
     */
    public static Class<?> getNamespaceFromXmlResource(Resource resource) {
        try {
            XPathParser xPathParser = new XPathParser(resource.getInputStream(), true, null, new XMLMapperEntityResolver());
            XNode mapperNode = xPathParser.evalNode(MAPPER.getCode());
            String namespace = mapperNode.getStringAttribute(NAMESPACE.getCode());
            return loadClass(namespace);
        } catch (Exception e) {
            throw new MyBatisProException();
        }
    }

    private static Resource createEmptyResource(Class<?> mapper) {
        String namespace = mapper.getName();
        String xml =
                "<?xml version='1.0' encoding='UTF-8' ?>\n" +
                        "<!DOCTYPE mapper\n" +
                        "        PUBLIC '-//mybatis.org//DTD Mapper 3.0//EN'\n" +
                        "        'http://mybatis.org/dtd/mybatis-3-mapper.dtd'>\n" +
                        "<mapper namespace='" + namespace + "'></mapper>";
        return new ByteArrayResource(xml.getBytes(UTF_8));
    }

    /**
     * 处理通用crud方法
     */
    private static Set<Resource> processMapperMethods(Set<Resource> all) {
        return ofNullable(all).orElseGet(HashSet::new)
                .stream()
                .map(resource -> new MapperUtil(resource).parse())
                .collect(toSet());
    }

    public static Set<Resource> processSpecialMethods(Set<Resource> resources) {
        return resources.stream().map(resource -> {
            Class<?> mapperCls = getNamespaceFromXmlResource(resource);
            validateDuplicateMethods(mapperCls, resource);

            Document doc = createDocumentFromResource(resource);
            List<String> specialMethods = getSpecialMethods(mapperCls);
            if (!isEmpty(specialMethods)) {
                Class<?> entityCls = getTypeArgument(mapperCls);
                if (!hasAnnotation(entityCls, Table.class)) {
                    throw new MyBatisProException("实体" + entityCls.getName() + "必须包含@com.github.dreamroute.mybatis.pro.core.annotations.Table注解");
                }
                String tableName = getAnnotationValue(entityCls, Table.class);
                Map<String, String> name2Type = getMethodName2ReturnType(mapperCls);
                specialMethods.forEach(specialMethodName -> {
                    String methodName = null;
                    String sql = null;
                    MapperLabel mapperLabel = SELECT;
                    if (specialMethodName.startsWith("findBy")) {
                        methodName = specialMethodName.substring(6);
                        sql = "select * from ";
                    } else if (specialMethodName.startsWith("deleteBy")) {
                        methodName = specialMethodName.substring(8);
                        sql = "delete from ";
                        mapperLabel = DELETE;
                    } else if (specialMethodName.startsWith("countBy")) {
                        methodName = specialMethodName.substring(7);
                        sql = "select count(*) c from ";
                    } else if (specialMethodName.startsWith("existBy")) {
                        methodName = specialMethodName.substring(7);
                        sql = "select (case when count(*)=0 then 'false' ELSE 'true' end) from ";
                    }
                    sql += tableName + " where " + createCondition(methodName);

                    //  对于delete需要特殊处理，delete不需要设置resultType
                    String resultType = mapperLabel == DELETE ? null : name2Type.get(specialMethodName);
                    fillSqlNode(doc, mapperLabel, specialMethodName, resultType, sql, null, null);
                });
            }
            return createResourceFromDocument(doc);
        }).collect(toSet());
    }

    /**
     * 校验Mapper接口内的xxxBy方法不能与xml文件的方法有同名
     */
    private static void validateDuplicateMethods(Class<?> mapperCls, Resource resource) {
        XPathParser xPathParser;
        try {
            xPathParser = new XPathParser(resource.getInputStream(), true, null, new XMLMapperEntityResolver());
        } catch (Exception e) {
            throw new MyBatisProException("解析" + mapperCls.getName() + "失败!");
        }
        List<XNode> selectMethods = xPathParser.evalNodes("mapper/select");
        List<XNode> insertMethods = xPathParser.evalNodes("mapper/insert");
        List<XNode> updateMethods = xPathParser.evalNodes("mapper/update");
        List<XNode> deleteMethods = xPathParser.evalNodes("mapper/delete");
        List<XNode> methods = new ArrayList<>();
        methods.addAll(selectMethods);
        methods.addAll(insertMethods);
        methods.addAll(updateMethods);
        methods.addAll(deleteMethods);
        List<String> xmlMethodNames = methods.stream().map(node -> node.getStringAttribute(ID.getCode())).collect(toList());

        // BaseMapper的方法 + xxxBy方法
        Set<String> innerMethodNames = getInnerMethodNames();
        List<String> specialMethods = getSpecialMethods(mapperCls);
        innerMethodNames.addAll(specialMethods);

        innerMethodNames.retainAll(xmlMethodNames);
        if (!isEmpty(innerMethodNames)) {
            throw new MyBatisProException("不允许接口" + mapperCls.getName() + "的方法" + toJSONString(innerMethodNames) + "与" + resource.getFilename() + "文件中的方法重名");
        }
    }

    /**
     * 将方法名转换成sql语句
     *
     * @param methodName 方法名
     * @return 返回sql语句
     */
    private static String createCondition(String methodName) {
        return SqlUtil.createSql(methodName);
    }

}