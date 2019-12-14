package com.zxc.tmall.dao;

import com.zxc.tmall.pojo.Category;
import com.zxc.tmall.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @ClassName PropertyDAO
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/12/7 9:36
 * @Version 1.0
 **/
//PropertyDAO 继承了 JpaRepository。提供常见的CRUD，根据分类进行查询
public interface PropertyDAO extends JpaRepository<Property,Integer> {
    Page<Property> findByCategory(Category category, Pageable pageable);
    //通过分类获取所有属性集合的方法
    List<Property> findByCategory(Category category);
}
