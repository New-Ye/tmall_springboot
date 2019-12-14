package com.zxc.tmall.dao;

import java.util.List;
import com.zxc.tmall.pojo.Product;
import com.zxc.tmall.pojo.Property;
import com.zxc.tmall.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyValueDAO extends JpaRepository<PropertyValue,Integer>{
    //根据产品查询
    List<PropertyValue> findByProductOrderByIdDesc(Product product);
    //根据产品和属性获取PropertyValue对象
    PropertyValue getByPropertyAndProduct(Property property, Product product);

}