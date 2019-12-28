package com.zxc.tmall.comparator;

import java.util.Comparator;
import com.zxc.tmall.pojo.Product;

public class ProductPriceComparator implements Comparator<Product> {
	//价格比较器	把价格低的放前面
	@Override
	public int compare(Product p1, Product p2) {
		return (int) (p1.getPromotePrice()-p2.getPromotePrice());
	}

}

