package com.jhl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "manager")
@Data
public class ManagerConfig {


    private String address;

    private String getProxyAccountUrl;
    /**
     * 流量上报
     */
    private String reportFlowUrl;
}
