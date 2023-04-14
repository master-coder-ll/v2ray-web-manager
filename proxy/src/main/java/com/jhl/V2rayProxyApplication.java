package com.jhl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sun.security.action.GetPropertyAction;

import java.io.*;
import java.security.AccessController;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Slf4j
public class V2rayProxyApplication {
    private final static String V2RAY_RESTART_COMMAND = "systemctl restart v2ray";

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        try {
            Runtime.getRuntime().exec(V2RAY_RESTART_COMMAND).waitFor(5,TimeUnit.SECONDS);
            log.info("执行重启v2ray：{}", V2RAY_RESTART_COMMAND);
        } catch (Exception e) {
            log.error(" 重启v2ray失败,如果科学不了，一般都是启动先后次序导致，请手动重启v2ray", e);
        }

        //需要接受args，如果不加载不了自定义配置
        SpringApplication.run(V2rayProxyApplication.class, args);
    }


}
