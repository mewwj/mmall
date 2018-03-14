package com.mall.service;

import java.util.List;


import com.mall.common.ServerResponse;
import com.mall.pojo.Category;

public interface ICategoryService {
	ServerResponse addCategory(String CategoryName,Integer parentId);	
	
	ServerResponse updateCategory(Integer categoryId,String categoryName);
	
	ServerResponse<List<Category>> getChildrenParallelCategory(Integer CategoryId);
	
	ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
