package com.jhl.admin.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "email")
public class EmailConstant {
    private String userName;
    private String password;
    private String host;
    //验证码消息模板
    private String vCodeTemplate;
    private  Integer port;
    private  String overdueDate;
    private  String exceedConnections;
    private  Boolean startTlsEnabled;
}
