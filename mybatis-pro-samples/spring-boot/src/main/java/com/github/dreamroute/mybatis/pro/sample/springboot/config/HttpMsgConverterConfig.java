package com.github.dreamroute.mybatis.pro.sample.springboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.dreamroute.mybatis.pro.base.enums.EnumMarkerDeserializer;
import com.github.dreamroute.mybatis.pro.base.enums.EnumMarkerSerializerForWeb;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 描述：自定义HttpMessageConverter
 *
 * @author w.dehi.2021-12-17
 */
@Configuration
public class HttpMsgConverterConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 移除默认
        converters.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);

        // 定义枚举序列化、反序列化
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Enum.class, new EnumMarkerSerializerForWeb());
        simpleModule.addDeserializer(Enum.class, new EnumMarkerDeserializer());

        // 注册自定义module
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(simpleModule);

        // 自定义Converter
        MappingJackson2HttpMessageConverter c = new MappingJackson2HttpMessageConverter();
        c.setObjectMapper(mapper);

        // 将自定义Converter添加到列表的第一个
        converters.add(0, c);
    }

}


