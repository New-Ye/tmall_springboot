package com.zxc.tmall.service;


import com.zxc.tmall.dao.PropertyDAO;
import com.zxc.tmall.pojo.Category;
import com.zxc.tmall.pojo.Property;
import com.zxc.tmall.util.Page4Navigator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PropertyService {
   @Autowired
    PropertyDAO propertyDAO;
   @Autowired
    CategoryService categoryService;

    public void add(Property bean){
       propertyDAO.save(bean);
   }

    public void delete(int id){
       propertyDAO.delete(id);
   }

    public void update(Property bean){
        propertyDAO.save(bean);
    }

    public Property get(int id){
       return propertyDAO.findOne(id);
   }

    public Page4Navigator<Property> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<Property> pageFromJPA =propertyDAO.findByCategory(category,pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    public List<Property> listByCategory(Category category){
        return propertyDAO.findByCategory(category);
    }

}
