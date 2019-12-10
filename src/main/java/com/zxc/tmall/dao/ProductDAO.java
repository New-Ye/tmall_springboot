package com.zxc.tmall.dao;

import com.zxc.tmall.pojo.Category;
import com.zxc.tmall.pojo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName ProductDAO
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/12/10 17:14
 * @Version 1.0
 **/
public interface ProductDAO extends JpaRepository<Product,Integer>{
    Page<Product> findByCategory(Category category, Pageable pageable);
}
