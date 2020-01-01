package com.zxc.tmall.service;


import com.zxc.tmall.dao.ProductImageDAO;
import com.zxc.tmall.pojo.OrderItem;
import com.zxc.tmall.pojo.Product;
import com.zxc.tmall.pojo.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName ProductImageService
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/12/15 10:54
 * @Version 1.0
 **/

@Service
public class ProductImageService   {

    public static final String type_single = "single";
    public static final String type_detail = "detail";

    @Autowired ProductImageDAO productImageDAO;
    @Autowired ProductService productService;

    public void add(ProductImage bean) {
        productImageDAO.save(bean);

    }
    public void delete(int id) {
        productImageDAO.delete(id);
    }

    public ProductImage get(int id) {
        return productImageDAO.findOne(id);
    }

    public List<ProductImage> listSingleProductImages(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, type_single);
    }
    public List<ProductImage> listDetailProductImages(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, type_detail);
    }

    public void setFirstProdutImage(Product product) {
        List<ProductImage> singleImages = listSingleProductImages(product);
        if(!singleImages.isEmpty())
            product.setFirstProductImage(singleImages.get(0));
        else
            product.setFirstProductImage(new ProductImage()); //这样做是考虑到产品还没有来得及设置图片，但是在订单后台管理里查看订单项的对应产品图片。

    }
    public void setFirstProdutImages(List<Product> products) {
        for (Product product : products)
            setFirstProdutImage(product);
    }
    //在结算页面显示产品的第一张图片
    public void setFirstProdutImagesOnOrderItems(List<OrderItem> ois) {
        for (OrderItem orderItem : ois) {
            setFirstProdutImage(orderItem.getProduct());
        }
    }
}
