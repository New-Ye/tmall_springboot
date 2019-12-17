package com.zxc.tmall.web;

import com.zxc.tmall.pojo.User;
import com.zxc.tmall.service.UserService;
import com.zxc.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/12/17 21:47
 * @Version 1.0
 **/
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/users")
    public Page4Navigator<User> list(@RequestParam(value = "start",defaultValue = "0")int start,@RequestParam(value = "size",defaultValue = "5")int size)throws Exception{
        start=start<0?0:start;
        Page4Navigator<User> page = userService.list(start,size,5);
        return page;
    }

}
