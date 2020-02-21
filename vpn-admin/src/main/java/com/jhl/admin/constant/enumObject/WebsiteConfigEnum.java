package com.jhl.admin.constant.enumObject;

import lombok.Getter;

@Getter
public enum WebsiteConfigEnum {


    IS_NEED_INVITE_CODE("需要邀请码才能注册吗？", "IS_NEED_INVITE_CODE", "false","config"),
    VIP_CAN_INVITE("用户能邀请其他人注册吗？", "VIP_CAN_INVITE", "false","config"),
    SUBSCRIPTION_ADDRESS_PREFIX("订阅地址访问前缀","SUBSCRIPTION_ADDRESS_PREFIX","http://127.0.0.1/api","config");
    private String name;

    private String key;

    private String value;

    private String scope;

    WebsiteConfigEnum(String name, String key, String value, String scope) {
        this.name = name;
        this.key = key;
        this.value = value;
        this.scope=scope;
    }




}
