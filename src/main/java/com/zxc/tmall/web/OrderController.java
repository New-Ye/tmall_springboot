package com.zxc.tmall.web;

import com.zxc.tmall.pojo.Order;
import com.zxc.tmall.service.OrderItemService;
import com.zxc.tmall.service.OrderService;
import com.zxc.tmall.util.Page4Navigator;
import com.zxc.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

/**
 * @ClassName OrderController
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/12/22 10:28
 * @Version 1.0
 **/
@RestController
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    OrderItemService orderItemService;
    @GetMapping("/orders")
    public Page4Navigator<Order> list(@RequestParam(value = "start", defaultValue = "0") int start,@RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start<0?0:start;
        Page4Navigator<Order> page =orderService.list(start, size, 5);
        orderItemService.fill(page.getContent());
        orderService.removeOrderFromOrderItem(page.getContent());
        return page;
    }

    /**
     *1. 当订单状态是 waitDelivery的时候，就会出现发货按钮。
     *2. 点击发货按钮，导致deliveryOrder 函数被调用，会送异步请求到 deliveryOrder/id 这个地址去。 同时，隐藏这个按钮。其中e.target就是这个按钮。
     *3. OrderController 的 deliveryOrder 方法响应这个请求，修改状态并更新。
     *4. 然后返回一个 Result.success()，用于表明成功了
     **/
    @PutMapping("deliveryOrder/{oid}")
    public Object deliveryOrder(@PathVariable int oid) throws IOException {
        Order o = orderService.get(oid);
        o.setDeliveryDate(new Date());
        o.setStatus(OrderService.waitConfirm);
        orderService.update(o);
        return Result.success();
    }
}
