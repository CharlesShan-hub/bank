package com.bank.server.config;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 业务异常统一处理：IllegalArgumentException → 400 + {"message": "..."}
 * 前端 axios 拦截器读取 message 字段弹窗提示
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务层抛出的非法参数（用户名已存在、余额不足、金额非法等）
     * 统一包装成 HTTP 400 + JSON {"message": "..."}
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(IllegalArgumentException e) {
        return Map.of("message", e.getMessage() == null ? "请求参数错误" : e.getMessage());
    }
}
