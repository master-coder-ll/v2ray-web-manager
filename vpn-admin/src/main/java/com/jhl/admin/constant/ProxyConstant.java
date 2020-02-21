package com.jhl.admin.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "proxy")
@PropertySource(value = {"classpath:application.properties"}, encoding = "utf-8")
public class ProxyConstant {
    String authPassword;
    String subscriptionTemplate;
}
