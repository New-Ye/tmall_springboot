package com.zxc.tmall.service;

import com.zxc.tmall.dao.OrderDAO;
import com.zxc.tmall.pojo.Order;
import com.zxc.tmall.pojo.OrderItem;
import com.zxc.tmall.util.Page4Navigator;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * @ClassName OrderService
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/12/22 10:17
 * @Version 1.0
 **/

@Service
public class OrderService {
    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";

    @Autowired
    OrderDAO orderDAO;

    public Page4Navigator<Order> list(int start,int size,int navigatePages){
        Sort sort=new Sort(Sort.Direction.DESC,"id");
        Pageable pageable=new PageRequest(start,size,sort);
        Page pageFromJPA=orderDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);

    }

    public void removeOrderFromOrderItem(List<Order> orders){
        for (Order order:orders){
            removeOrderFromOrderItem(order);
        }
    }

    public void removeOrderFromOrderItem(Order order){
        List<OrderItem> orderItems=order.getOrderItems();
        for (OrderItem orderItem : orderItems){
            orderItem.setOrder(null);
            //因为productImage类有一个Product属性，而Product类中firstProductImage也是一个productImage，在SpringMVC中产生json数组的过程中也会出现无穷调用的堆栈异常。
            //解决方法在原来的removeOrderFromOrderItem增加一句把firstImage的product置空即可
            orderItem.getProduct().getFirstProductImage().setProduct(null);
        }
    }
    public Order get(int oid) {
        return orderDAO.findOne(oid);
    }

    public void update(Order bean) {
        orderDAO.save(bean);
    }

}