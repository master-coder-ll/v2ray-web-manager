package com.jhl.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "manager")
@Data
public class ManagerConstant {


    private String address;

    private String getProxyAccountUrl;
    /**
     * 流量上报
     */
    private String reportFlowUrl;

    private String reportOverConnectionLimitUrl;
}
