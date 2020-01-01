package com.zxc.tmall.dao;

import com.zxc.tmall.pojo.Order;
import com.zxc.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * @ClassName OrderDAO
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/12/22 10:00
 * @Version 1.0
 **/
public interface OrderDAO extends JpaRepository<Order,Integer> {

    //用来获取那些某个用户的订单，但是状态又不是 "delete" 的订单。 "delete" 是作为状态调用的时候传进来的
    public List<Order> findByUserAndStatusNotOrderByIdDesc(User user, String status);
}
