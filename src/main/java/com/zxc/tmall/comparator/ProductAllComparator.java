package com.zxc.tmall.comparator;
import java.util.Comparator;
import com.zxc.tmall.pojo.Product;
public class ProductAllComparator implements Comparator<Product>{
	//综合比较器	把销量x评价高的放前面
	@Override
	public int compare(Product p1, Product p2) {
		return p2.getReviewCount()*p2.getSaleCount()-p1.getReviewCount()*p1.getSaleCount();
	}

}