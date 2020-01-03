package com.zxc.tmall.service;

import com.zxc.tmall.dao.PropertyDAO;
import com.zxc.tmall.dao.PropertyValueDAO;
import com.zxc.tmall.pojo.Product;
import com.zxc.tmall.pojo.Property;
import com.zxc.tmall.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName PropertyValueService
 * @Description TODO
 * @Author 爱学习真好
 * @Date 2019/12/14 23:17
 * @Version 1.0
 **/

@Service
@CacheConfig(cacheNames="propertyValues")
public class PropertyValueService {
    @Autowired
    PropertyValueDAO propertyValueDAO;
    @Autowired
    PropertyService propertyService;
    @CacheEvict(allEntries=true)
    public void update(PropertyValue bean){
        propertyValueDAO.save(bean);
    }
    //对于PropertyValue的管理，没有增加，只有修改。
    //所以需要通过初始化来进行自动地增加，以便于后面的修改
    public void init(Product product) {
        //根据产品获取分类，然后获取这个分类下的所有属性集合
        List<Property> propertys= propertyService.listByCategory(product.getCategory());
        //用属性id和产品id去查询，看看这个属性和这个产品，是否已经存在属性值了
        for (Property property: propertys) {
            PropertyValue propertyValue = getByPropertyAndProduct(product, property);
            // 如果不存在，那么就创建一个属性值，并设置其属性和产品，接着插入到数据库中
            if(null==propertyValue){
                propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDAO.save(propertyValue);
            }
            //这样就完成了属性值的初始化
        }
    }
    @Cacheable(key="'propertyValues-one-pid-'+#p0.id+ '-ptid-' + #p1.id")
    public PropertyValue getByPropertyAndProduct(Product product, Property property) {
        return propertyValueDAO.getByPropertyAndProduct(property,product);
    }
    @Cacheable(key="'propertyValues-pid-'+ #p0.id")
    public List<PropertyValue> list(Product product){
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }
}
