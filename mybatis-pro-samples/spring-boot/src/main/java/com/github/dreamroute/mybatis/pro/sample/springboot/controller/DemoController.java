package com.github.dreamroute.mybatis.pro.sample.springboot.controller;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.Demo;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * 描述：测试枚举类型的HttpMessageConverter
 *
 * @author w.dehi.2021-12-20
 */
@RestController
@RequestMapping(produces = {APPLICATION_JSON_VALUE})
public class DemoController {

    /**
     * 测试枚举的序列化和反序列化
     *
     * @param demo 请求参数中带有枚举类型的反序列化
     * @return 返回值中带有枚举类型的序列化
     */
    @PostMapping("/demo/enumTest")
    Demo dm(@RequestBody Demo demo) {
        return demo;
    }

    @PostMapping("/demo/arrTest")
    DemoDTO arr(@RequestBody DemoDTO demoDTO) {
        return demoDTO;
    }

    @Data
    public static class DemoDTO implements Serializable {
        private List<Demo> demos;
    }

}
