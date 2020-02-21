package com.jhl.admin.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "email")
@PropertySource(value = {"classpath:application.properties"}, encoding = "utf-8")
public class EmailConstant {
    private String userName;
    private String password;
    private String host;
    //验证码消息模板
    private String vCodeTemplate;
    private  int port;
    private  String overdueDate;
    private  String exceedConnections;
}
