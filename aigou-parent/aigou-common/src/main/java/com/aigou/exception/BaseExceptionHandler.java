package com.aigou.exception;

import entity.Result;
import entity.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author anchao
 * @date 2020/2/18 19:18
 */
@Slf4j
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
        log.error("进入通用异常处理="+e.getMessage());
        e.printStackTrace();
        return new Result(false, StatusCode.ERROR, e.getMessage());
    }
}
