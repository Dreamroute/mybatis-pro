package com.github.dreamroute.mybatis.pro.sample.springboot.interceptor;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static com.github.dreamroute.mybatis.pro.service.interceptor.ProxyUtil.getOriginObj;

public class ProxyUtilTest {

    @Test
    void getOriginTest() {
        Car car = (Car) Proxy.newProxyInstance(Car.class.getClassLoader(), new Class<?>[]{Car.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return "w.dehai";
            }
        });

        Car originObj = getOriginObj(car);
        System.err.println(originObj);
    }

}

interface Car {
}