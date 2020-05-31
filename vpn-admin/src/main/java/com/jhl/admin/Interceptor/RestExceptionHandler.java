package com.jhl.admin.Interceptor;

import com.ljh.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {


    @ExceptionHandler(ConstraintViolationException.class)
    public Result validationErrorHandler(ConstraintViolationException ex) {
        List<String> errorInformation = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        return Result.builder().code(500).message(errorInformation.toString()).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result validationErrorHandler(MethodArgumentNotValidException ex) {
        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        return Result.builder().code(500).message( fieldErrors.get(0).getDefaultMessage()).build();
    }
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception e) {
         log.error(" handler controllerError:",e);

        return Result.builder().code(500).message(e.getMessage()).build();
    }

}
