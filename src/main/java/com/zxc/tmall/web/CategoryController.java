package com.zxc.tmall.web;

import com.zxc.tmall.pojo.Category;
import com.zxc.tmall.service.CategoryService;
import com.zxc.tmall.util.ImageUtil;
import com.zxc.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


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
    @PostMapping("/categories")
    public Object add(Category bean, MultipartFile image, HttpServletRequest request) throws Exception{
        //通过CategoryService 保存到数据库
        categoryService.add(bean);
        //接受上传图片，并保存到 img/category目录下
        saveOrUpdateImageFile(bean, image, request);
        return bean;
    }
    private void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
        File imageFolder=new File(request.getServletContext().getRealPath("img/category"));
        //文件名使用新增分类的id
        File file=new File(imageFolder,bean.getId()+".jpg");
        //如果目录不存在，需要创建
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdir();
            //image.transferTo 进行文件复制
            image.transferTo(file);
            // 调用ImageUtil的change2jpg 进行文件类型强制转换为 jpg格式
            BufferedImage img= ImageUtil.change2jpg(file);
            //保存图片
            ImageIO.write(img,"jpg",file);
        }
    }

}
