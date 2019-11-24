package com.zxc.tmall.dao;

import com.zxc.tmall.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName CategoryDAO
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/11/24 10:14
 * @Version 1.0
 **/
public interface CategoryDAO extends JpaRepository<Category,Integer>{

}
