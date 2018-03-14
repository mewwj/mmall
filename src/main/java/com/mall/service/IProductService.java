package com.mall.service;

import com.github.pagehelper.PageInfo;
import com.mall.common.ServerResponse;
import com.mall.pojo.Product;
import com.mall.vo.ProductDetailVo;

public interface IProductService {
		ServerResponse saveOrUpdate(Product product);
		
		ServerResponse<String> setSaleStatus(Integer productId,Integer status);
		
		ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

		ServerResponse getProductList(int pageNum,int pageSize);
		
		ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);
		
		ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
		
		ServerResponse<PageInfo> getProductByKeyWordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
