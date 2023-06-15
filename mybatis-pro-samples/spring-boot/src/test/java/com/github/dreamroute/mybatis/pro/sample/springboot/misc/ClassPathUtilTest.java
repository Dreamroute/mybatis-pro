package com.github.dreamroute.mybatis.pro.sample.springboot.misc;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;

import static com.github.dreamroute.mybatis.pro.base.util.ClassPathUtil.resolvePackage;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 描述：通配符路径解析测试
 *
 * @author w.dehi.2022-02-28
 */
class ClassPathUtilTest {
    @Test
    void resolvePackageTest() {
        String path = "com.github.dreamroute.mybatis.pro.sample.springboot.*.mapper";
        String a = "com.github.dreamroute.mybatis.pro.sample.springboot.a.mapper";
        String b = "com.github.dreamroute.mybatis.pro.sample.springboot.b.mapper";
        String[] pkg = resolvePackage(path);
        HashSet<String> result = Sets.newHashSet(pkg);
        assertTrue(result.contains(a));
        assertTrue(result.contains(b));

        String basePath = "com.github.dreamroute.mybatis.pro.sample.springboot.c";
        String[] pkgs = resolvePackage(basePath);
        if (!ObjectUtils.isEmpty(pkgs)) {
            for (String p : pkgs) {
                System.err.println(p);
            }
        }
    }
}
