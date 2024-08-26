package com.github.dreamroute.mybatis.pro.sample.springboot.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dreamroute.mybatis.pro.sample.springboot.domain.Demo;
import com.github.dreamroute.mybatis.pro.sample.springboot.domain.Gender;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
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
     * 测试枚举的序列化和反序列化（包括嵌套类型）
     *
     * @return 返回值中带有枚举类型的序列化
     */
    @PostMapping("/demo/enumTest")
    EnumDto enumTest(@RequestBody @Valid EnumDto enumDto) {
        return enumDto;
    }

    @PostMapping("/demo/arrTest")
    DemoDTO arr(@RequestBody DemoDTO demoDTO) {
        return demoDTO;
    }

    @PostMapping("/demo/deserializeEnumArrTest")
    DeserializeEnumDTO deserializeEnumArrTest(@RequestBody DeserializeEnumDTO deserializeEnumDTO) {
        System.err.println(deserializeEnumDTO);
        return deserializeEnumDTO;
    }

    @PostMapping("/demo/deserializeDateTest")
    DeserializeDateDTO deserializeDateTest(@RequestBody DeserializeDateDTO req) {
        System.err.println(req);
        return req;
    }

    @Data
    public static class DemoDTO implements Serializable {
        private List<Demo> demos;
    }

    @Data
    public static class DeserializeEnumDTO implements Serializable {
        private Collection<Gender> genders;
        private List<String> names;
        private Gender gender;
        private List<Sub> subs;
        private Date birthday;
        private String[] roles;
    }

    @Data
    public static class DeserializeDateDTO implements Serializable {
        private Date date;
        private LocalDate localDate;
        private LocalDateTime localDateTime;
        @JsonProperty("user_name")
        private String userName;
    }

    @Data
    public static class Sub implements Serializable {
        private Long id;
        private String name;
        private Gender gender;
    }

    @Data
    public static class EnumDto implements Serializable {
        private Long id;
        private Gender gender;
        private EnumNestDto enumNestDto;
    }

    @Data
    public static class EnumNestDto implements Serializable {
        private Long id;
        private Gender gender;
        private EnumNestDto inner;
    }

}
