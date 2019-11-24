package com.zxc.tmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName Application
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/11/24 10:44
 * @Version 1.0
 **/

@SpringBootApplication
public class Application {
    //启动类,没有它启动不了
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
