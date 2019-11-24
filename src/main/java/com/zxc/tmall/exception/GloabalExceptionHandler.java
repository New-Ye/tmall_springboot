package com.zxc.tmall.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName GloabalExceptionHandler
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/11/24 10:56
 * @Version 1.0
 **/

@RestController
@ControllerAdvice
public class GloabalExceptionHandler {
    //全局异常处理
    //在处理删除父类信息的时候，因为外键约束的存在，而导致违反约束
    @ExceptionHandler(value = Exception.class)
    public String defaultErrorHandler(HttpServletRequest req,Exception e) throws Exception{
        e.printStackTrace();
        Class constraintViolationException = Class.forName("org.hibernate.exception.ConstraintViolationException");
        if(null!=e.getCause() && constraintViolationException==e.getCause().getClass()) {
            return "违反了约束，多半是外键约束";
        }
        return e.getMessage();
    }

}
