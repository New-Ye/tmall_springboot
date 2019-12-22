package com.zxc.tmall.dao;

import com.zxc.tmall.pojo.Order;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @ClassName OrderDAO
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/12/22 10:00
 * @Version 1.0
 **/
public interface OrderDAO extends JpaRepository<Order,Integer> {

}
