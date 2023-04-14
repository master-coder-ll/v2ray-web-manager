package com.ljh.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class ProxyAccount implements Serializable {
    public static final  Long M = 1024 * 1024L;
    /**
     * 账号ID,用于标识 连接属于谁
     */
    private Integer accountId;

    /**
     * 客户端访问的域名，校验
     */
    private String host;
    /**
     * 用于v2ray path的使用
     */
    private String accountNo;
    /**
     * "id": "fcecbd2b-3a34-4201-bd3d-7c67d89c26ba"
     * uuid
     */
    private String id;
    /**
     * "alterId": 32
     * 兼用新版 alterId 0
     * alterID 是 v2ray 的一个安全特性，用于防止被识别和干扰。但是在最新的 v2ray 版本中，已经引入了更先进的安全机制，所以不再需要 alterID 了。
     */
    private Integer alterId = 64;
    /**
     * 0
     */
    private Integer level = 0;

    private String email;

    private String inBoundTag;

    private Long upTrafficLimit = 1 * M;

    private Long downTrafficLimit = 1 * M;

    /**
     * 最大连接数
     */
    private Integer maxConnection = 30;

    private String v2rayHost = "127.0.0.1";
    private int v2rayPort = 6001;
    private int v2rayManagerPort = 62789;
    /**
     * 代理中间件的ip
     */
    private  String proxyIp;


}
