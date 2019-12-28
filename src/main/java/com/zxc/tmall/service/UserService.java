package com.zxc.tmall.service;

import com.zxc.tmall.dao.UserDAO;
import com.zxc.tmall.pojo.User;
import com.zxc.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;



/**
 * @ClassName UserService
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/12/17 21:42
 * @Version 1.0
 **/
@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    //判断某个名称是否已经被使用过
    public boolean isExist(String name){
        User user=getByName(name);
        return null!=user;
    }
    private User getByName(String name) {
        return userDAO.findByName(name);
    }

    //登录
    public User get(String name,String password){
        return userDAO.getByNameAndPassword(name,password);
    }

    public Page4Navigator<User> list(int start,int size,int navigatePages){
        Sort sort=new Sort(Sort.Direction.DESC,"id");
        Pageable pageable=new PageRequest(start,size,sort);
        Page pageFromJPA=userDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    //名称为重复，添加进数据库
    public void add(User user){
        userDAO.save(user);
    }
}
