package com.jhl;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication

public class V2rayProxyApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        //需要接受args，如果不加载不了自定义配置
        SpringApplication.run(V2rayProxyApplication.class,args);
    }


}
