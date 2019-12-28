package com.zxc.tmall.comparator;

import java.util.Comparator;
import com.zxc.tmall.pojo.Product;

public class ProductReviewComparator implements Comparator<Product> {
	//人气比较器	把评价数量多的放前面
	@Override
	public int compare(Product p1, Product p2) {
		return p2.getReviewCount()-p1.getReviewCount();
	}

}
