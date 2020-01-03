package com.zxc.tmall.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.zxc.tmall.pojo.Product;

public interface ProductESDAO extends ElasticsearchRepository<Product,Integer>{

}