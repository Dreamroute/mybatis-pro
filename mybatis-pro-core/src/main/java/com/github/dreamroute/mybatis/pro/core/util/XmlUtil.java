package com.github.dreamroute.mybatis.pro.core.util;

import com.github.dreamroute.mybatis.pro.core.exception.MyBatisProException;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import static cn.hutool.core.util.ClassUtil.loadClass;
import static com.github.dreamroute.mybatis.pro.core.consts.MapperLabel.MAPPER;
import static com.github.dreamroute.mybatis.pro.core.consts.MapperLabel.NAMESPACE;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 描述：xml文件工具类
 *
 * @author w.dehi.2022-04-01
 */
public class XmlUtil {
    private XmlUtil() {}

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
            throw new MyBatisProException("解析mapper.xml文件获取namespace出错", e);
        }
    }

    public static Resource createEmptyResource(Class<?> mapper) {
        String namespace = mapper.getName();
        String xml =
                "<?xml version='1.0' encoding='UTF-8' ?>\n" +
                        "<!DOCTYPE mapper\n" +
                        "        PUBLIC '-//mybatis.org//DTD Mapper 3.0//EN'\n" +
                        "        'http://mybatis.org/dtd/mybatis-3-mapper.dtd'>\n" +
                        "<mapper namespace='" + namespace + "'></mapper>";
        return new ByteArrayResource(xml.getBytes(UTF_8));
    }
}
