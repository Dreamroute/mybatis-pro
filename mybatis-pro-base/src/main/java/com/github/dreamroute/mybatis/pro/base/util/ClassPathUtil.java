package com.github.dreamroute.mybatis.pro.base.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 描述：解析路径通配符
 *
 * @author w.dehi.2022-02-28
 */
@Slf4j
public class ClassPathUtil {

    public static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    /**
     * 获取指定路径下的所有包名
     *
     * @param packagePath 指定路径
     * @return 返回该路径下的所有包名
     */
    public static String[] resolvePackage(String packagePath) {
        // 资源路径解析器
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        // 元数据读取
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
        // 解析路径
        packagePath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(packagePath) + "/" + DEFAULT_RESOURCE_PATTERN;
        Set<String> result = new HashSet<>(); // 别名包路径集合
        try {
            Resource[] resources = resolver.getResources(packagePath); // 根据路径 读取所有的类资源
            if (resources != null && resources.length > 0) {
                MetadataReader metadataReader;
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        metadataReader = metadataReaderFactory.getMetadataReader(resource); // 读取类的信息，每个 Resource 都是一个类资源
                        try {
                            result.add(Class.forName(metadataReader.getClassMetadata().getClassName()).getPackage().getName()); // 存储类对应的包路径
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toArray(new String[0]);
    }
}
