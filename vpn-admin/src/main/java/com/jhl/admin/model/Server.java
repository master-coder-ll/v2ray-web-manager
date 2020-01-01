package com.jhl.admin.model;

import lombok.*;

import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Server extends BaseEntity implements Serializable {


    private String serverName;
    //v.kxsw2019.cf
    private String clientDomain;
    private Integer clientPort = 443;
    private Boolean supportTLS = true;

    //proxy中间件管理 ip port;

    private String proxyIp = "127.0.0.1";
    private Integer proxyPort = 8091;
    //v2ray 开放 的 ip 和端口
    private String v2rayIp = "127.0.0.1";
    private Integer v2rayManagerPort=62789;
    private Integer v2rayPort = 6001;




    private String protocol;

    //流量倍数
    private Double multiple;

    //说明
    private String desc;
    //服务器状态
    private Integer status;

    private String inboundTag;

    //ws路径
    private String wsPath ="/ws/%s/";


}

