package com.github.dreamroute.mybatis.pro.core.util;

import com.github.dreamroute.mybatis.pro.core.MyBatisProException;
import com.github.dreamroute.mybatis.pro.core.consts.MapperLabel;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;

import java.nio.charset.StandardCharsets;
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
public class ResourceUtil {

    private static final Logger logger = LoggerFactory.getLogger(ResourceUtil.class);

    private ResourceUtil() {}

    public static Resource[] processFindByMethods(Resource[] resources, Set<String> mapperPackages) {

        Set<Class<?>> mappers = ClassUtil.getInterfacesFromPackage(mapperPackages);
        Set<Class<?>> existXmlMapper = getExistMappers(resources);
        mappers.removeAll(existXmlMapper);

        Set<Resource> allResources = new HashSet<>();
        Set<Resource> extraResource = mappers.stream().map(ResourceUtil::createResource).collect(Collectors.toSet());
        allResources.addAll(extraResource);
        allResources.addAll(Arrays.asList(resources));

        // 处理findBy方法
        Set<Resource> all = processFindByMethods(allResources);

        // 处理通用crud
        Set<Resource> result = processMyBatisProMethods(all);

        return result.toArray(new Resource[result.size()]);
    }

    private static Set<Class<?>> getExistMappers(Resource[] resources) {
        return Arrays.stream(Optional.ofNullable(resources).orElse(new Resource[0]))
                .map(ResourceUtil::getMapperByResource)
                .collect(Collectors.toSet());
    }

    public static Class<?> getMapperByResource(Resource resource) {
        try {
            XPathParser xPathParser = new XPathParser(resource.getInputStream(), true, null, new XMLMapperEntityResolver());
            XNode mapperNode = xPathParser.evalNode(MapperLabel.MAPPER.getCode());
            String namespace = mapperNode.getStringAttribute(MapperLabel.NAMESPACE.getCode());
            return ClassUtils.forName(namespace, ResourceUtil.class.getClass().getClassLoader());
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

    public static Set<Resource> processFindByMethods(Set<Resource> resources) {
        return resources.stream().map(resource -> {
            Class<?> mapperCls = getMapperByResource(resource);
            List<String> findByMethodNames = ClassUtil.getFindByMethods(mapperCls);
            try {
                // 获取mapper.xml的<select>标签的方法，并且与所有findBy方法对比，移除交集，也就是id与接口的findBy名称相同，那么xml文件的优先级更高
                List<XNode> selectNodes = new XPathParser(resource.getInputStream(), true, null, new XMLMapperEntityResolver()).evalNodes("mapper/select");
                List<String> selectNames = selectNodes.stream().map(node -> node.getStringAttribute(MapperLabel.ID.getCode())).collect(Collectors.toList());
                findByMethodNames.removeAll(selectNames);
            } catch (Exception e) {
                throw new MyBatisProException("解析" + mapperCls.getName() + "失败!");
            }

            Document doc = DocumentUtil.createDocumentFromResource(resource);
            if (!CollectionUtils.isEmpty(findByMethodNames)) {
                Map<String, String> name2Type = ClassUtil.getName2Type(mapperCls);
                findByMethodNames.forEach(findByMethodName -> {
                    String sql = "select * from " + ClassUtil.getTableNameFromEntity(name2Type.get(findByMethodName)) + " where " + createCondition(findByMethodName);
                    DocumentUtil.fillSqlNode(doc, MapperLabel.SELECT, findByMethodName, name2Type.get(findByMethodName), sql);
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
        return Optional.ofNullable(all).orElse(new HashSet<Resource>())
                .stream()
                .map(resource -> new MapperUtil(resource).parse())
                .collect(Collectors.toSet());
    }

}