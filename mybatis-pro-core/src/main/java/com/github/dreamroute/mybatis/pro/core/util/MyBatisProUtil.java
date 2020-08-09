package com.github.dreamroute.mybatis.pro.core.util;

import com.github.dream.mybatis.pro.sdk.Mapper;
import com.github.dreamroute.mybatis.pro.core.exception.MyBatisProException;
import com.github.dreamroute.mybatis.pro.core.consts.MapperLabel;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;

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

        // 处理findBy, deleteBy, updateBy, countBy, existBy方法
        Set<Resource> all = processSpecialMethods(allResources);

        // 处理通用crud
        Set<Resource> result = processMyBatisProMethods(all);

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

    public static Set<Resource> processSpecialMethods(Set<Resource> resources) {
        return resources.stream().map(resource -> {
            Class<?> mapperCls = getMapperByResource(resource);
            List<String> specialMethods = ClassUtil.getSpecialMethods(mapperCls);
            try {
                // 获取mapper.xml的<select>标签的方法，移除交集，也就是用户自定义方法优先级高
                XPathParser xPathParser = new XPathParser(resource.getInputStream(), true, null, new XMLMapperEntityResolver());
                List<XNode> selectMethods = xPathParser.evalNodes("mapper/select");
                List<XNode> updateMethods = xPathParser.evalNodes("mapper/update");
                List<XNode> deleteMethods = xPathParser.evalNodes("mapper/delete");
                List<XNode> methods = new ArrayList<>();
                methods.addAll(selectMethods);
                methods.addAll(deleteMethods);
                methods.addAll(deleteMethods);

                List<String> methodNames = methods.stream().map(node -> node.getStringAttribute(MapperLabel.ID.getCode())).collect(Collectors.toList());
                specialMethods.removeAll(methodNames);
            } catch (Exception e) {
                throw new MyBatisProException("解析" + mapperCls.getName() + "失败!");
            }

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
                    } else if (specialMethodName.startsWith("delete")) {
                        methodName = specialMethodName.substring(6);
                        sql = "delete from " + tableName;
                    } else if (specialMethodName.startsWith("update")) {
                        methodName = specialMethodName.substring(6);
                        sql = "update " + tableName + "set ";
                    } else if (specialMethodName.startsWith("count")) {
                        methodName = specialMethodName.substring(5);
                        sql = "select count(*) c from " + tableName;
                    } else if (specialMethodName.startsWith("exist")) {
                        methodName = specialMethodName.substring(5);
                        sql = "select (case when count(*)=0 then 'false' ELSE 'true' end) from " + tableName;
                    }
                    sql +=  " where " + createCondition(methodName);
                    DocumentUtil.fillSqlNode(doc, MapperLabel.SELECT, specialMethodName, name2Type.get(specialMethodName), sql, null, null);
                });
            }
            return DocumentUtil.createResourceFromDocument(doc);
        }).collect(Collectors.toSet());
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

    /**
     * 处理通用crud方法
     */
    private static Set<Resource> processMyBatisProMethods(Set<Resource> all) {
        return Optional.ofNullable(all).orElse(new HashSet<>())
                .stream()
                .map(resource -> new MapperUtil(resource).parse())
                .collect(Collectors.toSet());
    }

}