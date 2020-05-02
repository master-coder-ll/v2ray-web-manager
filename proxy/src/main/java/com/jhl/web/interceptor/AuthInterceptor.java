package com.jhl.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.jhl.common.constant.ProxyConstant;
import com.ljh.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    ProxyConstant proxyConstant;

    private static final String AUTH_NAME = "Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authPassword = DigestUtils.md5Hex(proxyConstant.getAuthPassword());
        String reqAuthPassword = request.getHeader(AUTH_NAME);
        if (!StringUtils.hasLength(reqAuthPassword) || !authPassword.equals(reqAuthPassword)) {
            Result result = Result.builder().code(401).message("认证失败").build();
            response.setHeader("content-type", "application/json;charset=UTF-8");
            response.getOutputStream().write(JSON.toJSONBytes(result));
            return false;
        }

        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }

}
