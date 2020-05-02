package com.jhl.common.constant;

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


    private  String globalConnectionStatUrl = "/report/connectionStat?accountNo={accountNo}&host={host}&count={count}";
}
