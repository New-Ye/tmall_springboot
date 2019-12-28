package com.zxc.tmall.dao;

import com.zxc.tmall.pojo.Order;
import com.zxc.tmall.pojo.OrderItem;
import com.zxc.tmall.pojo.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemDAO extends JpaRepository<OrderItem,Integer> {
    List<OrderItem> findByOrderOrderByIdDesc(Order order);
    //根据产品获取OrderItem
    List<OrderItem> findByProduct(Product product);
}
