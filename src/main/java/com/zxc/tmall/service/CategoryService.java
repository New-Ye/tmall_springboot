package com.zxc.tmall.service;

import com.zxc.tmall.dao.CategoryDAO;
import com.zxc.tmall.pojo.Category;
import java.util.List;

import com.zxc.tmall.pojo.Product;
import com.zxc.tmall.util.Page4Navigator;
import org.elasticsearch.common.lucene.all.AllEntries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
@CacheConfig(cacheNames = "categories")
public class CategoryService {
    //自动装配CategoryDAO对象
    @Autowired
    CategoryDAO categoryDAO;

    //查询所有产品分类
    @Cacheable(key="'categories-all'")
    public List<Category> list(){
        //创建一个 Sort 对象，表示通过 id 倒排序， 然后通过 categoryDAO进行查询。
        Sort sort=new Sort(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }
    //提供分页功能
    @Cacheable(key="'categories-page-'+#p0+ '-' + #p1")
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size,sort);
        Page pageFromJPA =categoryDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }
    //新增分类方法add
    @CacheEvict(allEntries=true)
    public void add(Category bean){
        categoryDAO.save(bean);
    }

    //删除分类
    @CacheEvict(allEntries=true)
    public void delete(int id){
        categoryDAO.delete(id);
    }
    //获取地址栏id
    @Cacheable(key = "'categories-one-'+ #p0")
    public Category get(int id){
        Category c = categoryDAO.findOne(id);
        return c;
    }
    //编辑分类名和图片
    @CacheEvict(allEntries=true)
    public void update(Category bean){
        categoryDAO.save(bean);
    }

    //删除Product对象上的 分类
    //因为在对分类做序列还转换为 json 的时候，会遍历里面的 products, 然后遍历出来的产品上，又会有分类，就开始无穷遍历
    //要在前端业务上，没有通过产品获取分类的业务
    //而在这里去掉，就没事了。 只要在前端业务上，没有通过产品获取分类的业务，去掉也没有关系
    public void removeCategoryFromProduct(List<Category> cs) {
        for (Category category : cs) {
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category){
        List<Product> products=category.getProducts();
        if (null!=products){
            for (Product product:products){
                product.setCategory(null);
            }
        }
        List<List<Product>> productsByRow=category.getProductsByRow();
        if (null!=productsByRow){
            for (List<Product>ps:productsByRow){
                for (Product  p:ps){
                    p.setCategory(null);
                }
            }
        }
    }

}
