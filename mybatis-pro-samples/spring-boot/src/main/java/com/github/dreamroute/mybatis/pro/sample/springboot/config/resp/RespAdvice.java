package com.github.dreamroute.mybatis.pro.sample.springboot.config.resp;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.Dict;
import com.github.dreamroute.mybatis.pro.sample.springboot.service.DictService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.github.dreamroute.mybatis.pro.sample.springboot.util.DictUtil.convert;
import static com.github.dreamroute.mybatis.pro.sample.springboot.util.DictUtil.dict2Map;

/**
 * @author w.dehai
 */
@Configuration
@AllArgsConstructor
public class RespAdvice implements ResponseBodyAdvice<Object> {

    private final DictService dictService;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Method method = returnType.getMethod();
        Class<?> type = method.getReturnType();
        return !(Objects.equals(type, String.class) || Objects.equals(type, byte[].class));
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 如果是String，也直接返回，目的是用于开发者可以手动控制返回值
        if (body != null && !(body instanceof String)) {
            body = processDict(body);
        }
        return RespUtil.respSuccess(body);
    }

    private Object processDict(Object body) {
        List<Dict> all = dictService.selectAll();
        Map<String, Map<Integer, String>> dictMap = dict2Map(all);
        return convert(body, dictMap);
    }
}
