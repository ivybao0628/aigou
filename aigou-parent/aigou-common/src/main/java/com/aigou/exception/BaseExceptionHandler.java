package com.aigou.exception;

import entity.Result;
import entity.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author anchao
 * @date 2020/2/18 19:18
 */
@ControllerAdvice
public class BaseExceptionHandler {

    /***
     * 未知异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        System.out.println("进入通用异常处理--------->");
        return new Result(false, StatusCode.ERROR, e.getMessage());
    }
}