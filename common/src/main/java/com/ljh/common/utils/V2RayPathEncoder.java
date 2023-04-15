package com.ljh.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class V2RayPathEncoder {
    private V2RayPathEncoder() {
    }
    public  static  String encoder(String account,String host,String password){

        return DigestUtils.md5Hex(account+host+password);
    }
}
