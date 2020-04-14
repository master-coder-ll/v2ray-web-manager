package com.jhl.constant;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "proxy")
@Data
@Slf4j
public class ProxyConstant {
    private int localPort;
    private String remoteHost;
    private int remotePort;
    private int maxConnections;
    private String env;
    private String queueSavePath;
    private String authPassword;

    public void setAuthPassword(String authPassword) {
        log.info("The authPassword you set is :{},如果authPassword如设置的不一致，配置有问题。" +
                "[If the authPassword is inconsistent with the settings, there is a configuration problem]",authPassword );
        this.authPassword = authPassword;
    }
}
