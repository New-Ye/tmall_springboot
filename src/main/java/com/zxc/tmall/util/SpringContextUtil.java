package com.zxc.tmall.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * @ClassName SpringContextUtil
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2020/1/3 10:19
 * @Version 1.0
 **/
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private SpringContextUtil() {

    }

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext){
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

}
/*  1. 分页的时候，会带上cid,表示是某个分类下的产品
    2. 如果在ProductService的一个方法里，调用另一个 缓存管理 的方法，不能够直接调用，需要通过一个工具，再拿一次 ProductService， 然后再调用。
            比如：listByCategory 方法本来就是 ProductService 的方法，却不能直接调用。
            因为 springboot 的缓存机制是通过切面编程 aop来实现的。
            从fill方法里直接调用 listByCategory 方法， aop 是拦截不到的，也就不会走缓存了。
            所以要通过这种 绕一绕 的方式故意诱发 aop, 这样才会想我们期望的那样走redis缓存*/