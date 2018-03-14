package com.mall.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.dao.CategoryMapper;
import com.mall.dao.ProductMapper;
import com.mall.pojo.Category;
import com.mall.pojo.Product;
import com.mall.service.ICategoryService;
import com.mall.service.IProductService;
import com.mall.util.DateTimeUtil;
import com.mall.util.PropertiesUtil;
import com.mall.vo.ProductDetailVo;
import com.mall.vo.ProductListVo;
/**@author wenjiewang
 * 
 */

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
		
		@Autowired
		private ProductMapper productMapper;
		
		@Autowired
		private CategoryMapper categoryMapper;
		
		@Autowired
		private ICategoryService iCategoryService;
	
	public ServerResponse saveOrUpdate(Product product){
		if(product != null){
		        if(StringUtils.isNotBlank(product.getSubImages())){
		        	String[] subImageArray =product.getSubImages().split(",");
		        	  if(subImageArray.length>0){
		        		       product.setMainImage(subImageArray[0]);
		        	  }
		        }
		        if(product.getId() != null){
		        	int rowCount =productMapper.updateByPrimaryKey(product);
		        	if(rowCount>0){
		        		return ServerResponse.createBySuccess("产品信息更新成功！");
		        	}else{
		        		return ServerResponse.createByErrorMessage("更新产品失败！");
		        	}
		        }else{
		        	int rowCount=productMapper.insert(product);
		        	if(rowCount>0){
		        		return ServerResponse.createBySuccess("新增产品成功！");
		        	}else{
		        		return ServerResponse.createByErrorMessage("新增产品失败！");
		        	}
		        }
		        
		}
			return ServerResponse.createByErrorMessage("新增或更新产品参数不正确 ！");
		
	}
	
	public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
		if(productId == null||status==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product= new Product();
		product.setId(productId);
		product.setStatus(status);
		int rowCount=productMapper.updateByPrimaryKeySelective(product);
		if(rowCount>0){
			return ServerResponse.createBySuccess("修改产品销售状况成功！");
		}
		return ServerResponse.createBySuccess("修改产品销售状况失败！");	
	}
	
	public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
		if(productId == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if(product == null){
			return ServerResponse.createByErrorMessage("产品已下架或者 删除！");
		}
//		简单的  -->vo对象  vlaue-object
//		复杂情况  pojo-bo(bussiness object  业务层的业务对象)-vo(view )object
		ProductDetailVo  productDetailVo = assambleProductDetailVo(product);
		return ServerResponse.createBySuccess(productDetailVo);
	}
	
	
	private ProductDetailVo assambleProductDetailVo(Product product){
		ProductDetailVo productDetailVo=new ProductDetailVo();
		productDetailVo.setId(product.getId());
		productDetailVo.setSubtitle(product.getSubtitle());
		productDetailVo.setPrice(product.getPrice());
		productDetailVo.setMainImage(product.getMainImage());
		productDetailVo.setCategoryId(product.getCategoryId());
		productDetailVo.setDetail(product.getDetail());
		productDetailVo.setName(product.getName());
		productDetailVo.setStock(product.getStock());
		
		//赋值imageHost  image前缀，图片服务器
		productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "ftp://127.0.0.1/img/"));
		//赋值CategoryId
		Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
		if(category == null){
			productDetailVo.setParentCategoryId(0);
		}else{
			productDetailVo.setParentCategoryId(category.getParentId());
		}
		 //赋值creatatime
		productDetailVo.setCreatTime(DateTimeUtil.dateToStr(product.getCreateTime()));
		productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
		
		return productDetailVo;
	}
	  
	//分页的业务实现 
	public ServerResponse getProductList(int pageNum,int pageSize){
		//startPage ---start
		//填写sql的查询逻辑
		//pageHelper的收尾
		PageHelper.startPage(pageNum, pageSize);
		List<Product> productList= productMapper.selectList();
		
		List<ProductListVo> productListVoList =Lists.newArrayList();
		for (Product productItem : productList) {
			ProductListVo productListVo=assembleProductListVo(productItem);
			productListVoList.add(productListVo);
		}
		PageInfo pageResult=new PageInfo(productList);
		
		pageResult.setList(productListVoList);
		return ServerResponse.createBySuccess(pageResult);
	}
	  
	//productlist VO的组装方法
	private ProductListVo  assembleProductListVo(Product product){
		ProductListVo  productListVo =  new ProductListVo();
		productListVo.setId(product.getId());
		productListVo.setName(product.getName());	
		productListVo.setCategory(product.getCategoryId());
		productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","ftp://127.0.0.1/img/"));
		productListVo.setMainImage(product.getMainImage());
		productListVo.setPrice(product.getPrice());
		productListVo.setSubtitle(product.getSubtitle());
		productListVo.setStatus(product.getStatus());
		return productListVo;
	}
	
	//商品搜索业务逻辑 
	 public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
		 PageHelper.startPage(pageNum, pageSize);
		 if(StringUtils.isNotBlank(productName)){
			 productName= new StringBuilder().append("%").append(productName).append("%").toString();
		 }
		 List<Product> productList =productMapper.selectByNameAndProductId(productName, productId);
		 	//将productList --->转为ProductListVo
			List<ProductListVo> productListVoList =Lists.newArrayList();
			for (Product productItem : productList) {
				ProductListVo productListVo=assembleProductListVo(productItem);
				productListVoList.add(productListVo);
			}
			
			System.out.println("productname----"+productName);
			System.out.println(productId);
			System.out.println(pageNum);
			System.out.println(pageSize);
			//分页
			PageInfo pageResult=new PageInfo(productList);
			pageResult.setList(productListVoList);
			return ServerResponse.createBySuccess(pageResult);
			
	 }
	
	public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
		if(productId != null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		if(product == null){
			return ServerResponse.createByErrorMessage("产品已下架或者 删除！");
		}
//		简单的  -->vo对象  vlaue-object
//		复杂情况  pojo-bo(bussiness object  业务层的业务对象)-vo(view )object
		if( product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
				return ServerResponse.createByErrorMessage("产品下架或者已经删除！");
		}
		ProductDetailVo  productDetailVo = assambleProductDetailVo(product);
		return ServerResponse.createBySuccess(productDetailVo);
	}
	
	public ServerResponse<PageInfo> getProductByKeyWordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
		if(StringUtils.isBlank(keyword) && categoryId == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), 
													ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		List<Integer> categoryIdList= new ArrayList<Integer>();
		
		if(categoryId != null){}
				Category category =categoryMapper.selectByPrimaryKey(categoryId);
				if(category == null && StringUtils.isBlank(keyword)){
					   //没有该分类，并且没有关键字，这时候返回一个空的集合 不报错 
					PageHelper.startPage(pageNum, pageSize);
					List<ProductListVo> ProduvtListVoList = Lists.newArrayList();
					PageInfo pageInfo =new PageInfo(ProduvtListVoList);
					return ServerResponse.createBySuccess(pageInfo);
				}
				categoryIdList =iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
		if(StringUtils.isNotBlank(keyword)){
			keyword=new StringBuilder().append("%").append(keyword).append("%").toString();
		}
		PageHelper.startPage(pageNum,pageSize);
		//排序处理
		if(StringUtils.isNotBlank(orderBy)){
			if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
					String[] orderByArray = orderBy.split("_");
					PageHelper.orderBy(orderByArray[0]+""+orderByArray[1]);
		}
	}
	List<Product> productList =productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,
											categoryIdList.size()==0?null:categoryIdList);
 	
	List<ProductListVo> ProductListVoList=Lists.newArrayList();
	for (Product product : productList) {
		ProductListVo productListVo =assembleProductListVo(product);
		ProductListVoList.add(productListVo);
	}
	
	PageInfo pageInfo =new PageInfo(productList);
	pageInfo.setList(ProductListVoList);
	return ServerResponse.createBySuccess(pageInfo);
	
	
}
}
