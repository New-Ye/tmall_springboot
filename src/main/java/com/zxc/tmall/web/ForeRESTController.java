package com.zxc.tmall.web;

import com.zxc.tmall.comparator.*;
import com.zxc.tmall.pojo.*;
import com.zxc.tmall.service.*;
import com.zxc.tmall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName ForeRESTController
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/12/25 9:59
 * @Version 1.0
 **/
@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;

    @GetMapping("/forehome")
    public Object home(){
        //查询所有分类
        List<Category> cs=categoryService.list();
        //为这些分类填充产品集合
        productService.fill(cs);
        //为这些分类填充推荐产品集合
        productService.fillByRow(cs);
        //移除产品里的分类信息，以免出现重复递归
        categoryService.removeCategoryFromProduct(cs);
        //返回这个 categoreis集合，随即被转换为 json 数组
        return cs;
    }

    @PostMapping("/foreregister")
    public Object register(@RequestBody User user) {
        String name =  user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);

        if(exist){
            String message ="用户名已经被使用,不能使用";
            return Result.fail(message);
        }
        userService.add(user);
        return Result.success();
    }

    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name =  userParam.getName();
        name = HtmlUtils.htmlEscape(name);

        User user =userService.get(name,userParam.getPassword());
        if(null==user){
            String message ="账号密码错误";
            return Result.fail(message);
        }
        else{
            session.setAttribute("user", user);
            return Result.success();
        }
    }

    @GetMapping("/foreproduct/{pid}")
    //获取参数pid
    public Object product(@PathVariable("pid") int pid) {
        //根据pid获取Product 对象product
        Product product = productService.get(pid);
        //根据对象product，获取这个产品对应的单个图片集合
        List<ProductImage> productSingleImages = productImageService.listSingleProductImages(product);
        //根据对象product，获取这个产品对应的详情图片集合
        List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);
        // 获取产品的所有属性值
        List<PropertyValue> pvs = propertyValueService.list(product);
        //获取产品对应的所有的评价
        List<Review> reviews = reviewService.list(product);
        //设置产品的销量和评价数量
        productService.setSaleAndReviewNumber(product);
        productImageService.setFirstProdutImage(product);
        //把上述取值放在 map 中
        Map<String,Object> map= new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", reviews);
        //通过 Result 把这个 map 返回到浏览器去
        return Result.success(map);
    }

    @GetMapping("forecheckLogin")
    public Object checkLogin( HttpSession session) {
        User user =(User)session.getAttribute("user");
        if(null!=user)
            return Result.success();
        return Result.fail("未登录");
    }

    //按价格，人气，上架人气等排序
    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable int cid,String sort) {
        Category c = categoryService.get(cid);
        productService.fill(c);
        productService.setSaleAndReviewNumber(c.getProducts());
        categoryService.removeCategoryFromProduct(c);

        if(null!=sort){
            switch(sort){
                case "review":
                    Collections.sort(c.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(c.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(c.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(c.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(c.getProducts(),new ProductAllComparator());
                    break;
            }
        }

        return c;
    }

    //未登陆时的搜索
    @PostMapping("foresearch")
    public Object search( String keyword){
        if(null==keyword)
            keyword = "";
        List<Product> ps= productService.search(keyword,0,20);
        productImageService.setFirstProdutImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }
}
