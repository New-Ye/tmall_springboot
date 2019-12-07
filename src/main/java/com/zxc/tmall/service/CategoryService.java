package com.zxc.tmall.service;

import com.zxc.tmall.dao.CategoryDAO;
import com.zxc.tmall.pojo.Category;
import java.util.List;
import com.zxc.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

/**
 * @ClassName CategoryService
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/11/24 10:17
 * @Version 1.0
 **/

@Service
public class CategoryService {
    //自动装配CategoryDAO对象
    @Autowired
    CategoryDAO categoryDAO;
    //查询所有产品分类
    public List<Category> list(){
        //创建一个 Sort 对象，表示通过 id 倒排序， 然后通过 categoryDAO进行查询。
        Sort sort=new Sort(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }
    //提供分页功能
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size,sort);
        Page pageFromJPA =categoryDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }
    //新增分类方法add
    public void add(Category bean){
        categoryDAO.save(bean);
    }

    //删除分类
    public void delete(int id){
        categoryDAO.delete(id);
    }
    //获取地址栏id
    public Category get(int id){
        Category c = categoryDAO.findOne(id);
        return c;
    }
    //编辑分类名和图片
    public void update(Category bean){
        categoryDAO.save(bean);
    }

}
