package com.zxc.tmall.service;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.zxc.tmall.dao.ProductDAO;
import com.zxc.tmall.es.ProductESDAO;
import com.zxc.tmall.pojo.Category;
import com.zxc.tmall.pojo.OrderItem;
import com.zxc.tmall.pojo.Product;
import com.zxc.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName ProductService
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/12/10 17:17
 * @Version 1.0
 **/
@Service
@CacheConfig(cacheNames="products")
public class ProductService {

    @Autowired
    ProductDAO productDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ProductESDAO productESDAO;
    /*增加，删除，修改的时候，除了通过 ProductDAO 对数据库产生影响之外，
    还要通过 ProductESDAO 同步到 elasticsearch里*/
    @CacheEvict(allEntries=true)
    public void add(Product bean){
        productDAO.save(bean);
        productESDAO.save(bean);
    }
    @CacheEvict(allEntries=true)
    public void delete(int id){
        productDAO.delete(id);
        productESDAO.delete(id);
    }
    @CacheEvict(allEntries=true)
    public void update(Product bean){
        productDAO.save(bean);
        productESDAO.save(bean);
    }
    @Cacheable(key="'products-one-'+ #p0")
    public Product get(int id){
        return productDAO.findOne(id);
    }
    @Cacheable(key="'products-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Product> list(int cid,int start,int size,int navigatePages){
        Category category=categoryService.get(cid);
        Sort sort=new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<Product> pageFromJPA =productDAO.findByCategory(category,pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    //为多个分类填充产品集合
    public void fill(List<Category> categorys) {
        for (Category category : categorys) {
            fill(category);
        }
    }
    //为分类填充产品集合
    public void fill(Category category){
        List<Product> products=listByCategory(category);
        productImageService.setFirstProdutImages(products);
        category.setProducts(products);
    }

    //为多个分类填充推荐产品集合
    //即把分类下的产品集合，按照8个为一行，拆成多行，以利于后续页面上进行显示
    public void fillByRow(List<Category> categorys) {
        int productNumberEachRow = 8;
        for (Category category : categorys) {
            List<Product> products =  category.getProducts();
            List<List<Product>> productsByRow =  new ArrayList<>();
            for (int i = 0; i < products.size(); i+=productNumberEachRow) {
                int size = i+productNumberEachRow;
                size= size>products.size()?products.size():size;
                List<Product> productsOfEachRow =products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }
    //查询某个分类下的所有产品
    @Cacheable(key="'products-cid-'+ #p0.id")
    public List<Product> listByCategory(Category category) {
        return productDAO.findByCategoryOrderById(category);
    }

    //设置产品销量
    public void setSaleAndReviewNumber(Product product){
        int saleCount= orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);
        int reviewCount=reviewService.getCount(product);
        product.setReviewCount(reviewCount);
    }

    //设置产品评论数量
    public void setSaleAndReviewNumber(List<Product> products){
        for (Product product:products){
            setSaleAndReviewNumber(product);
        }
    }

    public List<Product> search(String keyword, int start, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        List<Product> products =productDAO.findByNameLike("%"+keyword+"%",pageable);
        return products;
    }

    //以前查询是模糊查询，现在通过 ProductESDAO 到 elasticsearch 中进行查询了
    private void initDatabase2ES() {
        Pageable pageable = new PageRequest(0, 5);
        Page<Product> page =productESDAO.findAll(pageable);
        if(page.getContent().isEmpty()) {
            List<Product> products= productDAO.findAll();
            for (Product product : products) {
                productESDAO.save(product);
            }
        }
    }
}
