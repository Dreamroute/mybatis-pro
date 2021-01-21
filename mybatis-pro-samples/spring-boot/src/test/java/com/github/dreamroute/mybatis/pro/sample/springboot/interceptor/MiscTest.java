package com.github.dreamroute.mybatis.pro.sample.springboot.interceptor;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.User;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.InterceptorChain;
import org.apache.ibatis.plugin.Invocation;
import org.junit.jupiter.api.Test;

class MiscTest {

    @Test
    void mmm() {
        Page page = new Page();
        User user = new User();
        Object o = page.plugin(user);
        System.err.println(o);
    }

}

class Page implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return null;
    }
}
