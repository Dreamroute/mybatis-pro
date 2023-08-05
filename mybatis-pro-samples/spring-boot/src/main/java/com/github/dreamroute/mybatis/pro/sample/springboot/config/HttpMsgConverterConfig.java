package com.github.dreamroute.mybatis.pro.sample.springboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.dreamroute.mybatis.pro.base.codec.date.DateDeserializer;
import com.github.dreamroute.mybatis.pro.base.codec.date.DateSerializer;
import com.github.dreamroute.mybatis.pro.base.codec.enums.EnumMarkerDeserializer;
import com.github.dreamroute.mybatis.pro.base.codec.enums.EnumMarkerDeserializerForCollection;
import com.github.dreamroute.mybatis.pro.base.codec.enums.EnumMarkerSerializerForExtra;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collection;
import java.util.Date;
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

        // 注册自定义module
        SimpleModule simpleModule = new SimpleModule();

        // 枚举序列化、反序列化
        simpleModule.addSerializer(Enum.class, new EnumMarkerSerializerForExtra());
        simpleModule.addDeserializer(Enum.class, new EnumMarkerDeserializer());
        simpleModule.addDeserializer(Collection.class, new EnumMarkerDeserializerForCollection());

        // 日期序列化、反序列化
        simpleModule.addSerializer(Date.class, new DateSerializer());
        simpleModule.addDeserializer(Date.class, new DateDeserializer());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(simpleModule);

        // 自定义Converter
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper);

        // 移除默认并且将自定义Converter添加到列表的第一个
        converters.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
        converters.add(0, converter);
    }

}


