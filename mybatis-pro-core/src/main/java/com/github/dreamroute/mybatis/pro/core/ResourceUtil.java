package com.github.dreamroute.mybatis.pro.core;

import com.github.dreamroute.mybatis.pro.core.consts.MapperLabel;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.io.ResolverUtil.IsA;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.persistence.Table;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

    private static final Logger log = LoggerFactory.getLogger(ResourceUtil.class);

    private ResourceUtil() {}

    public static Resource[] parseResource(Resource[] resources, Set<String> mapperPackages) {

        Set<Class<?>> mappers = getMappersFromPackages(mapperPackages);
        Set<Class<?>> existXmlMapper = getExistMappers(resources);
        mappers.removeAll(existXmlMapper);

        Set<Resource> allResources = new HashSet<>();
        Set<Resource> extraResource = null;
        if (!CollectionUtils.isEmpty(mappers)) {
            extraResource = mappers.stream().map(ResourceUtil::createResource).collect(Collectors.toSet());
            allResources.addAll(extraResource);
        }
        if (!ObjectUtils.isEmpty(resources)) {
            allResources.addAll(Arrays.asList(resources));
        }

        // 处理findBy方法
        Set<Resource> all = parseResource(allResources);

        // 处理通用crud
        Set<Resource> result = processAllResources(all);

        return result.toArray(new Resource[result.size()]);
    }

    /**
     * 获取mapper接口
     *
     * @param mapperPackages 包名
     * @return 返回包内所有mapper接口
     */
    private static Set<Class<?>> getMappersFromPackages(Set<String> mapperPackages) {
        return mapperPackages.stream().map(pkgName -> {
            ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
            resolverUtil.find(new IsA(Object.class), pkgName);
            return resolverUtil.getClasses();
        }).flatMap(Set::stream).filter(cls -> cls.isInterface()).collect(Collectors.toSet());
    }

    private static Set<Class<?>> getExistMappers(Resource[] resources) {
        if (!ObjectUtils.isEmpty(resources)) {
            return Arrays.stream(resources).map(ResourceUtil::getMapperByResource).collect(Collectors.toSet());
        }
        return new HashSet<>();
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

    public static Set<Resource> parseResource(Set<Resource> resources) {
        Set<Resource> result = new HashSet<>();
        if (!CollectionUtils.isEmpty(resources)) {
            for (Resource resource : resources) {
                try {
                    XPathParser xPathParser = new XPathParser(resource.getInputStream(), true, null, new XMLMapperEntityResolver());
                    XNode mapperNode = xPathParser.evalNode(MapperLabel.MAPPER.getCode());
                    String namespace = mapperNode.getStringAttribute(MapperLabel.NAMESPACE.getCode());
                    Class<?> mapperCls = ClassUtils.forName(namespace, ResourceUtil.class.getClass().getClassLoader());

                    // 获取接口的findBy开头的方法
                    List<String> names = ClassUtil.getFindByMethods(mapperCls);
                    if (names != null && !names.isEmpty()) {
                        // 获取mapper.xml的<select>标签的方法，并且与所有findBy方法对比，移除交集，也就是id与接口的findBy名称相同，那么xml文件的优先级更高
                        List<XNode> selectNodes = xPathParser.evalNodes("mapper/select");
                        if (selectNodes != null && !selectNodes.isEmpty()) {
                            List<String> selectNames = selectNodes.stream().map(node -> node.getStringAttribute(MapperLabel.ID.getCode())).collect(Collectors.toList());
                            names.removeAll(selectNames);
                        }

                        // 真正需要创建sql的findBy方法
                        if (names != null && !names.isEmpty()) {
                            // Map<findBy方法名, findBy方法返回值>
                            Map<String, String> name2Type = ClassUtil.getName2Type(mapperCls);
                            Document doc = createDoc(resource);
                            for (String name : names) {
                                // 1.<select>
                                Element selectElement = doc.createElement(MapperLabel.SELECT.getCode());

                                // 2.<select>sql</select>
                                String tableName = getTableName(name2Type.get(name));
                                String sql = "select * from " + tableName + " where " + createCondition(name);
                                Text sqlText = doc.createTextNode(sql);
                                selectElement.appendChild(sqlText);

                                // 3.<select id="xxx">sql</select>
                                Attr id = doc.createAttribute(MapperLabel.ID.getCode());
                                id.setValue(name);
                                selectElement.setAttributeNode(id);

                                // 3.<select id="xxx" resultType="xxx">sql</select>
                                Attr resultType = doc.createAttribute(MapperLabel.RESULT_TYPE.getCode());
                                resultType.setValue(name2Type.get(name));
                                selectElement.setAttributeNode(resultType);

                                /**
                                 * 4.
                                 * <mapper>
                                 *     <select id="xxx" resultType="xxx">sql</select>
                                 * </mapper>
                                 */
                                doc.getElementsByTagName(MapperLabel.MAPPER.getCode()).item(0).appendChild(selectElement);
                            }

                            result.add(DocumentUtil.createResourceFromDocument(doc));
                        }
                    }

                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        return result;
    }

    public static Document createDoc(Resource resource) {
        try {
            // 改写resource，加入findBy方法的<select>标签
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setValidating(false);
            documentBuilderFactory.setNamespaceAware(false);
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            // 此处需要设置EntityResolver，以便从classpath寻找dtd文件进行解析，否则会从网路下载，很慢并且可能会connect timeout
            builder.setEntityResolver(new XMLMapperEntityResolver());
            return builder.parse(resource.getInputStream());
        } catch (Exception e) {
            throw new MyBatisProException("创建Document失败");
        }
    }

    /**
     * 根据实体获取表名
     *
     * @param entityStr 实体
     * @return 返回表名
     */
    public static String getTableName(String entityStr) {
        try {
            Class<?> entityCls = ClassUtils.forName(entityStr, null);
            Table anno = entityCls.getAnnotation(Table.class);
            return anno.name();
        } catch (Exception e) {
            throw new IllegalArgumentException("获取表名失败，entity需要本@Table注解标注", e);
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

    /**
     * 处理通用crud方法
     */
    private static Set<Resource> processAllResources(Set<Resource> all) {
        return Optional.ofNullable(all).orElse(new HashSet<Resource>())
                .stream()
                .map(resource -> new MapperUtil(resource).parse())
                .collect(Collectors.toSet());
    }

}