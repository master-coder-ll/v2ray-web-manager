package com.jhl.admin.Interceptor;

import com.ljh.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception e) {
         log.error(" handler controllerError:",e);

        return Result.builder().code(500).message(e.getMessage()).build();
    }

}
