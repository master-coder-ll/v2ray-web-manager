package com.jhl.admin.constant;

import lombok.Getter;

@Getter
public enum WebsiteConfigKV {


    IS_NEED_INVITE_CODE("需要邀请码才能注册吗？", "IS_NEED_INVITE_CODE", "false","config"),
    VIP_CAN_INVITE("用户能邀请其他人注册吗？", "VIP_CAN_INVITE", "false","config");

    private String name;

    private String key;

    private String value;

    private String scope;

    WebsiteConfigKV(String name, String key, String value, String scope) {
        this.name = name;
        this.key = key;
        this.value = value;
        this.scope=scope;
    }




}
