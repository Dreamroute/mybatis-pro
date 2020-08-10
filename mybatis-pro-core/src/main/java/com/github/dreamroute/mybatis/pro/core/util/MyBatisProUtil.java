package com.github.dreamroute.mybatis.pro.core.util;

import com.github.dreamroute.mybatis.pro.core.consts.MapperLabel;
import com.github.dreamroute.mybatis.pro.core.exception.MyBatisProException;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 使用新的resource替换默认resource，并且创建接口Mapper无对应的mapper.xml
 *
 * @author w.dehai
 */
public class MyBatisProUtil {

    private MyBatisProUtil() {}

    public static Resource[] processMyBatisPro(Resource[] resources, Set<String> mapperPackages) {

        Set<Class<?>> mappers = ClassUtil.getInterfacesFromPackage(mapperPackages);
        Set<Class<?>> existXmlMapper = getExistMappers(resources);
        mappers.removeAll(existXmlMapper);

        Set<Resource> allResources = new HashSet<>();
        Set<Resource> extraResource = mappers.stream().map(MyBatisProUtil::createResource).collect(Collectors.toSet());
        allResources.addAll(extraResource);
        allResources.addAll(Arrays.asList(resources));

        // 处理findBy, deleteBy, countBy, existBy方法
        Set<Resource> all = processSpecialMethods(allResources);

        // 处理通用crud
        Set<Resource> result = processMapperMethods(all);

        return result.toArray(new Resource[0]);
    }

    private static Set<Class<?>> getExistMappers(Resource[] resources) {
        return Arrays.stream(Optional.ofNullable(resources).orElse(new Resource[0]))
                .map(MyBatisProUtil::getMapperByResource)
                .collect(Collectors.toSet());
    }

    public static Class<?> getMapperByResource(Resource resource) {
        try {
            XPathParser xPathParser = new XPathParser(resource.getInputStream(), true, null, new XMLMapperEntityResolver());
            XNode mapperNode = xPathParser.evalNode(MapperLabel.MAPPER.getCode());
            String namespace = mapperNode.getStringAttribute(MapperLabel.NAMESPACE.getCode());
            return ClassUtils.forName(namespace, MyBatisProUtil.class.getClassLoader());
        } catch (Exception e) {
            throw new MyBatisProException();
        }
    }

    private static Resource createResource(Class<?> mapper) {
        String mapperFullName = mapper.getName();
        String xml =
                "<?xml version='1.0' encoding='UTF-8' ?>\n" +
                        "<!DOCTYPE mapper\n" +
                        "        PUBLIC '-//mybatis.org//DTD Mapper 3.0//EN'\n" +
                        "        'http://mybatis.org/dtd/mybatis-3-mapper.dtd'>\n" +
                        "<mapper namespace='" + mapperFullName + "'></mapper>";
        return new ByteArrayResource(xml.getBytes(StandardCharsets.UTF_8));
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

    private static void validateDuplicateMethods(Class<?> mapperCls, Resource resource) {
        XPathParser xPathParser = null;
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
        List<String> methodNames = methods.stream().map(node -> node.getStringAttribute(MapperLabel.ID.getCode())).collect(Collectors.toList());
        Method[] mapperMethods = mapperCls.getMethods();
        List<String> mapperMethodNames = Arrays.stream(mapperCls.getMethods()).map(Method::getName).collect(Collectors.toList());
        mapperMethodNames.retainAll(methodNames);
        if (!CollectionUtils.isEmpty(mapperMethodNames)) {
            throw new MyBatisProException("不允许接口" + mapperCls.getName() + "的方法" + mapperMethodNames + "与xml文件中的方法重名");
        }
    }

    public static Set<Resource> processSpecialMethods(Set<Resource> resources) {
        return resources.stream().map(resource -> {
            Class<?> mapperCls = getMapperByResource(resource);
            List<String> specialMethods = ClassUtil.getSpecialMethods(mapperCls);
            validateDuplicateMethods(mapperCls, resource);

            Document doc = DocumentUtil.createDocumentFromResource(resource);
            if (!CollectionUtils.isEmpty(specialMethods)) {
                String entityCls = ClassUtil.getMapperGeneric(mapperCls);
                String tableName = ClassUtil.getTableNameFromEntity(entityCls);
                Map<String, String> name2Type = ClassUtil.getMethodName2ReturnType(mapperCls);
                specialMethods.forEach(specialMethodName -> {
                    String methodName = null;
                    String sql = null;
                    if (specialMethodName.startsWith("findBy")) {
                        methodName = specialMethodName.substring(6);
                        sql = "select * from " + tableName;
                    } else if (specialMethodName.startsWith("deleteBy")) {
                        methodName = specialMethodName.substring(8);
                        sql = "delete from " + tableName;
                    } else if (specialMethodName.startsWith("countBy")) {
                        methodName = specialMethodName.substring(7);
                        sql = "select count(*) c from " + tableName;
                    } else if (specialMethodName.startsWith("existBy")) {
                        methodName = specialMethodName.substring(7);
                        sql = "select (case when count(*)=0 then 'false' ELSE 'true' end) from " + tableName;
                    }
                    sql += " where " + createCondition(methodName);
                    DocumentUtil.fillSqlNode(doc, MapperLabel.SELECT, specialMethodName, name2Type.get(specialMethodName), sql, null, null);
                });
            }
            return DocumentUtil.createResourceFromDocument(doc);
        }).collect(Collectors.toSet());
    }

    /**
     * 处理通用crud方法
     */
    private static Set<Resource> processMapperMethods(Set<Resource> all) {
        return Optional.ofNullable(all).orElse(new HashSet<>())
                .stream()
                .map(resource -> new MapperUtil(resource).parse())
                .collect(Collectors.toSet());
    }

}