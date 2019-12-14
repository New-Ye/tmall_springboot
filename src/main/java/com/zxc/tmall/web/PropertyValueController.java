package com.zxc.tmall.web;

import com.zxc.tmall.pojo.Product;
import com.zxc.tmall.pojo.PropertyValue;
import com.zxc.tmall.service.ProductService;
import com.zxc.tmall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName PropertyValueController
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/12/14 23:35
 * @Version 1.0
 **/

@RestController
public class PropertyValueController {
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ProductService productService;

    @GetMapping("/products/{pid}/propertyValues")
    public List<PropertyValue> list(@PathVariable("pid") int pid) throws Exception {
        Product product = productService.get(pid);
        propertyValueService.init(product);
        List<PropertyValue> propertyValues = propertyValueService.list(product);
        return propertyValues;
    }

    public Object update(@RequestBody PropertyValue bean)throws Exception{
        propertyValueService.update(bean);
        return bean;
    }

}
