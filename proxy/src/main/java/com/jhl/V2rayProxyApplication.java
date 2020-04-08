package com.jhl;


import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.TimeZone;

@SpringBootApplication

public class V2rayProxyApplication {
    @Autowired
    FastJsonHttpMessageConverter fastJsonHttpMessageConverter;



    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(V2rayProxyApplication.class);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().removeIf(httpMessageConverter -> {
            String name = httpMessageConverter.getClass().getName();
            if (name.contains("json")) return true;
            else return false;
        });
        restTemplate.getMessageConverters().add(fastJsonHttpMessageConverter);
        return restTemplate;
    }
}
