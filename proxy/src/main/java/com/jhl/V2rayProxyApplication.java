package com.jhl;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class V2rayProxyApplication {
    public static void main(String[] args) {
        SpringApplication.run(V2rayProxyApplication.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate(){
        return  new RestTemplate();
    }
}
