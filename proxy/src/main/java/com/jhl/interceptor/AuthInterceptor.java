package com.jhl.interceptor;

import com.alibaba.fastjson.JSON;
import com.jhl.config.ProxyConfig;
import com.ljh.common.model.Result;
import lombok.extern.slf4j.Slf4j;
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
    ProxyConfig proxyConfig;

 private  static  final  String AUTH_NAME= "Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String auth = proxyConfig.getAuth();
        String reqAuth = request.getHeader(AUTH_NAME);
        if (!StringUtils.hasLength(reqAuth) ||  !auth.equals(reqAuth)){
            Result result = Result.builder().code(401).message("认证失败").build();
            response.setHeader("content-type","application/json;charset=UTF-8");
            response.getOutputStream().write(JSON.toJSONBytes(result));
            return false;
        }

        return true;
    }



    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

}
