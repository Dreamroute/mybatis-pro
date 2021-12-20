package com.github.dreamroute.mybatis.pro.sample.springboot.controller;

import com.github.dreamroute.mybatis.pro.sample.springboot.domain.Demo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;

import static com.github.dreamroute.mybatis.pro.sample.springboot.domain.Gender.FEMALE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 描述：枚举类型的序列化和反序列化测试
 *
 * @author w.dehi.2021-12-20
 */
@SpringBootTest
@AutoConfigureMockMvc
public class DemoControllerTest {

    @Resource
    private MockMvc mockMvc;

    @Test
    void enumTest() throws Exception {
        Demo demo = new Demo();
        demo.setGender(FEMALE);

        String request = "{\"gender\":  2}";
        mockMvc.perform(post("/demo/enumTest").content(request).contentType(APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gender.desc").value("女"));
    }

}
