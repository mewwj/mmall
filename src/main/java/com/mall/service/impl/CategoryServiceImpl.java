package com.mall.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mall.common.ServerResponse;
import com.mall.dao.CategoryMapper;
import com.mall.pojo.Category;
import com.mall.service.ICategoryService;
/**
 * 商品管理service
 * @author wenjiewang
 *
 */

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
	
	private org.slf4j.Logger logger=LoggerFactory.getLogger(CategoryServiceImpl.class);
	
	@Autowired
	private CategoryMapper categoryMapper;
	
		public ServerResponse addCategory(String CategoryName,Integer parentId){
			if(parentId == null ||  StringUtils.isBlank(CategoryName)  ){
				return ServerResponse.createByErrorMessage("添加品类参数错误！");
			}
			Category category = new Category();
			  category.setName(CategoryName);
			  category.setParentId(parentId);
			  category.setStatus(1);
			  
			  int rowCount =categoryMapper.insertSelective(category);
			  if(rowCount > 0){
				  return ServerResponse.createBySuccessMessage("添加品类成功 ！");  
			  }
			  return ServerResponse.createByErrorMessage("添加品类失败！");
			  
					  }
//		更新商品name
		public ServerResponse updateCategory(Integer categoryId,String categoryName){
			if(categoryId ==null || org.apache.commons.lang.StringUtils.isBlank(categoryName)){
				return ServerResponse.createByErrorMessage("更新商品名称参数错误！");
			}
			Category category=new Category();
			category.setName(categoryName);
			category.setId(categoryId);
			//根据主键 有选择的更新
			int rowCount=categoryMapper.updateByPrimaryKeySelective(category);
			if(rowCount > 0){
				return ServerResponse.createBySuccess("更新商品名称成功！");
			}else{
				return ServerResponse.createByErrorMessage("更新商品名称失败！");
			}
		}
		//获取子节点以及子节点以下 的节点
		public ServerResponse<List<Category>> getChildrenParallelCategory(Integer CategoryId){
			List<Category> categoryList=categoryMapper.selectCategoryChildByParentId(CategoryId);
 			//判断返回 的集合是否为空
			if(CollectionUtils.isEmpty(categoryList)){
 				logger.info("未找到当前分类的子分类！");
 			}
		  return ServerResponse.createBySuccess(categoryList);
		}
		
		
		/**
		 * 递归查询本节点的id以及孩子节点的id 
		 */
		public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
			Set<Category> categorySet = Sets.newHashSet();
			findChildCategory(categorySet,categoryId);
			List<Integer> categoryIdList =Lists.newArrayList();
			if(categoryId != null ){
				for(Category categoryItem:categorySet){
					categoryIdList.add(categoryItem.getId());
				}
			}
			return ServerResponse.createBySuccess(categoryIdList);
		}
		//递归函数 
		//递归算法 算出子节点
		private Set<Category> findChildCategory(Set<Category> categorySet ,Integer categoryId){
			 Category category=categoryMapper.selectByPrimaryKey(categoryId);
			 if(category != null){
				 categorySet.add(category);
			 }
			 //递归算法一定要有退出 的条件 
			 List<Category> categoryList = categoryMapper.selectCategoryChildByParentId(categoryId);
			 
			 for(Category categoryItem : categoryList){
				 findChildCategory(categorySet,categoryItem.getId());
			 }
			 return categorySet;
		}
		
}
