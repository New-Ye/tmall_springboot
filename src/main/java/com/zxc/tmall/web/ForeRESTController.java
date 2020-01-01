package com.zxc.tmall.web;

import com.zxc.tmall.comparator.*;
import com.zxc.tmall.pojo.*;
import com.zxc.tmall.service.*;
import com.zxc.tmall.util.Result;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;
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
    @Autowired
    OrderService orderService;

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

    /*  1. 获取参数pid
        2. 获取参数num
        3. 根据pid获取产品对象p
        4. 从session中获取用户对象user*/
    @GetMapping("forebuyone")
    public Object buyone(int pid, int num, HttpSession session) {
        return buyoneAndAddCart(pid,num,session);
    }

    private int buyoneAndAddCart(int pid, int num, HttpSession session) {
        Product product = productService.get(pid);
        int oiid = 0;
        User user =(User)  session.getAttribute("user");
        boolean found = false;
        /*  存在这个产品对应的OrderItem，并且还没有生成订单，即还在购物车中。 那么就应该在对应的OrderItem基础上，调整数量
            1 基于用户对象user，查询没有生成订单的订单项集合
            2 遍历这个集合
            3 如果产品是一样的话，就进行数量追加
            4 获取这个订单项的 id*/
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId()==product.getId()){
                oi.setNumber(oi.getNumber()+num);
                orderItemService.update(oi);
                found = true;
                oiid = oi.getId();
                break;
            }
        }
        if(!found){
            /*  如果不存在对应的OrderItem,那么就新增一个订单项OrderItem
                1 生成新的订单项
                2 设置数量，用户和产品
                3 插入到数据库
                4 获取这个订单项的 id*/
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setProduct(product);
            oi.setNumber(num);
            orderItemService.add(oi);
            oiid = oi.getId();
        }
        //返回当前订单项id
        return oiid;
    }

    /*  1. 通过字符串数组获取参数oiid
        为什么这里要用字符串数组试图获取多个oiid，而不是int类型仅仅获取一个oiid? 因为根据购物流程环节与表关系，结算页面还需要显示在购物车中选中的多条OrderItem数据，所以为了兼容从购物车页面跳转过来的需求，要用字符串数组获取多个oiid
        2. 准备一个泛型是OrderItem的集合ois
        3. 根据前面步骤获取的oiids，从数据库中取出OrderItem对象，并放入ois集合中
        4. 累计这些ois的价格总数，赋值在total上
        5. 把订单项集合放在session的属性 "ois" 上
        6. 把订单集合和total 放在map里
        7. 通过 Result.success 返回*/
    @GetMapping("forebuy")
    public Object buy(String[] oiid,HttpSession session){
        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;

        for (String strid : oiid) {
            int id = Integer.parseInt(strid);
            OrderItem oi= orderItemService.get(id);
            total +=oi.getProduct().getPromotePrice()*oi.getNumber();
            orderItems.add(oi);
        }

        productImageService.setFirstProdutImagesOnOrderItems(orderItems);

        session.setAttribute("ois", orderItems);

        Map<String,Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.success(map);
    }

    //添加到购物车
    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid,num,session);
        return Result.success();
    }
    /*  访问地址/forecart导致ForeRESTController.cart()方法被调用
        1. 通过session获取当前用户
        登录才访问，否则拿不到用户对象,会报错
        2. 获取为这个用户关联的订单项集合 ois
        3. 设置图片
        4. 返回这个订单项集合*/
    @GetMapping("forecart")
    public Object cart(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user);
        productImageService.setFirstProdutImagesOnOrderItems(ois);
        return ois;
    }
    //修改购物车中产品的数量
    @GetMapping("forechangeOrderItem")
    public Object changeOrderItem( HttpSession session, int pid, int num) {
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");

        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId()==pid){
                oi.setNumber(num);
                orderItemService.update(oi);
                break;
            }
        }
        return Result.success();
    }
    //删除购物车的产品
    @GetMapping("foredeleteOrderItem")
    public Object deleteOrderItem(HttpSession session,int oiid){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        orderItemService.delete(oiid);
        return Result.success();
    }
/*提交订单访问路径 /forecreateOrder, 导致ForeRESTController.createOrder 方法被调用
1. 从session中获取user对象
2. 根据当前时间加上一个4位随机数生成订单号
3. 根据上述参数，创建订单对象
4. 把订单状态设置为等待支付
5. 从session中获取订单项集合 ( 在结算功能的ForeRESTController.buy() ，订单项集合被放到了session中 )
7. 把订单加入到数据库，并且遍历订单项集合，设置每个订单项的order，更新到数据库
8. 统计本次订单的总金额
9. 返回总金额*/
    @PostMapping("forecreateOrder")
    public Object createOrder(@RequestBody Order order,HttpSession session){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderService.waitPay);
        List<OrderItem> ois= (List<OrderItem>)  session.getAttribute("ois");

        float total =orderService.add(order,ois);

        Map<String,Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);

        return Result.success(map);
    }

    /*forepayed地址导致 payed方法被调用：
        1 获取参数oid
        2 根据oid获取到订单对象order
        3 修改订单对象的状态和支付时间
        4 更新这个订单对象到数据库
        5 返回订单*/
    @GetMapping("forepayed")
    public Object payed(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }
    //查询所有订单的页面
    @GetMapping("forebought")
    public Object bought(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        List<Order> os= orderService.listByUserWithoutDelete(user);
        orderService.removeOrderFromOrderItem(os);
        return os;
    }
    /*  1. 点击确认收货后，访问地址/confirmPay 返回 confirmPay.html
        2. confirmPay.html 访问 foreconfirmPay 地址
        3. ForeRESTController.confirmPay()方法被调用
            1 获取参数oid
            2 通过oid获取订单对象o
            3 为订单对象填充订单项
            4 把订单项上的订单对象移除，否则会导致重复递归
            5 返回订单对象*/
    @GetMapping("foreconfirmPay")
    public Object confirmPay(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.cacl(o);
        orderService.removeOrderFromOrderItem(o);
        return o;
    }
    // 确认收货成功页面
    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed( int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.waitReview);
        o.setConfirmDate(new Date());
        orderService.update(o);
        return Result.success();
    }
    // 删除订单
    @PutMapping("foredeleteOrder")
    public Object deleteOrder(int oid){
        Order o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return Result.success();
    }

    /*  1. 通过点击评价按钮，来到路径/review，返回 review.html
        2. review.html 访问 forereview 地址
        3. ForeRESTController.review() 被调用
            3.1 获取参数oid
            3.2 根据oid获取订单对象o
            3.3 为订单对象填充订单项
            3.4 获取第一个订单项对应的产品,因为在评价页面需要显示一个产品图片，那么就使用这第一个产品的图片了。（这里没有对订单里的每种产品都评价，因为复杂度就比较高了，初学者学起来太吃力，有可能就放弃学习了，所以考虑到学习的平滑性，就仅仅提供对第一个产品的评价）
            3.5 获取这个产品的评价集合
            3.6 为产品设置评价数量和销量
            3.7 把产品，订单和评价集合放在map上
            3.8 通过 Result 返回这个map*/
    @GetMapping("forereview")
    public Object review(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.removeOrderFromOrderItem(o);
        Product p = o.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(p);
        productService.setSaleAndReviewNumber(p);
        Map<String,Object> map = new HashMap<>();
        map.put("p", p);
        map.put("o", o);
        map.put("reviews", reviews);

        return Result.success(map);
    }
    /*  在评价产品页面点击提交评价，就把数据提交到了/foredoreview路径，导致ForeRESTController.doreview方法被调用
        1. ForeRESTController.doreview()
            1.1 获取参数oid
            1.2 根据oid获取订单对象o
            1.3 修改订单对象状态
            1.4 更新订单对象到数据库
            1.5 获取参数pid
            1.6 根据pid获取产品对象
            1.7 获取参数content (评价信息)
            1.8 对评价信息进行转义，道理同注册ForeRESTController.register()
            1.9 从session中获取当前用户
            1.10 创建评价对象review
            1.11 为评价对象review设置 评价信息，产品，时间，用户
            1.12 增加到数据库
            1.13.返回成功
        2. reviewPage.html
        提交评价后， 通过调用 vue.showReviews = true; 把产品列表显示出来（默认是隐藏的）*/
    @PostMapping("foredoreview")
    public Object doreview( HttpSession session,int oid,int pid,String content) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.finish);
        orderService.update(o);

        Product p = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);

        User user =(User)  session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setProduct(p);
        review.setCreateDate(new Date());
        review.setUser(user);
        reviewService.add(review);
        return Result.success();
    }
}
