package com.zxc.tmall.service;

import com.zxc.tmall.dao.ReviewDAO;
import com.zxc.tmall.pojo.Product;
import com.zxc.tmall.pojo.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName ReviewService
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/12/28 11:00
 * @Version 1.0
 **/
@Service
@CacheConfig(cacheNames="reviews")
public class ReviewService {
    @Autowired
    ReviewDAO reviewDAO;
    @Autowired
    ProductService productService;

    //新增评论
    @CacheEvict(allEntries=true)
    public void add(Review review){
        reviewDAO.save(review);
    }

    //通过产品查询所有评论
    @Cacheable(key="'reviews-pid-'+ #p0.id")
    public List<Review> list(Product product){
        List<Review> result=reviewDAO.findByProductOrderByIdDesc(product);
        return result;
    }

    //获取评论总数
    @Cacheable(key="'reviews-count-pid-'+ #p0.id")
    public int getCount(Product product){
        return reviewDAO.countByProduct(product);
    }
}
