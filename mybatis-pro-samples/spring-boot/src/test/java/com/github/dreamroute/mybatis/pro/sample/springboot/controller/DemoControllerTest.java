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
class DemoControllerTest {

    private static final String BIRTHDAY_STR = "2022-05-05 15:05:12";

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

    /**
     * 数组类型的枚举和日期反序列化
     */
    @Test
    void arrTest() throws Exception {
        String request = "{\n" +
                "    \"demos\": [\n" +
                "        {\n" +
                "            \"gender\": 1,\n" +
                "            \"birthday\": \"" + BIRTHDAY_STR + "\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"gender\": 2,\n" +
                "            \"birthday\": \"" + BIRTHDAY_STR + "\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        mockMvc.perform(post("/demo/arrTest").content(request).contentType(APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.demos[0].gender.desc").value("男"))
                .andExpect(jsonPath("$.demos[1].gender.desc").value("女"))
                .andExpect(jsonPath("$.demos[0].birthday").value(BIRTHDAY_STR))
                .andExpect(jsonPath("$.demos[1].birthday").value(BIRTHDAY_STR));
    }

}
