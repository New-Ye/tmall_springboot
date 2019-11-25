package com.zxc.tmall.web;

import com.zxc.tmall.pojo.Category;
import com.zxc.tmall.service.CategoryService;
import com.zxc.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName CategoryController
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/11/24 10:37
 * @Version 1.0
 **/

//对每个方法的返回值都会直接转换为 json 数据格式。
@RestController
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/categories")
    public Page4Navigator<Category> list(@RequestParam(value = "start",defaultValue = "0")int start,
                               @RequestParam(value = "size",defaultValue = "5")int size) throws Exception{
        //对于categories 访问，会获取所有的 Category对象集合，并返回这个集合
        //这个集合，又会被自动转换为 JSON数组抛给浏览器。
        start=start<0?0:start;
        Page4Navigator<Category> page = categoryService.list(start,size,5);
        return page;
    }

}
