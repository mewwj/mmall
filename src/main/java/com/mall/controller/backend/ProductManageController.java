package com.mall.controller.backend;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.Product;
import com.mall.pojo.User;
import com.mall.service.IFileService;
import com.mall.service.IProductService;
import com.mall.service.IUserService;
import com.mall.util.PropertiesUtil;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
		
		@Autowired 
		private IUserService iUserService; 
		@Autowired 
		private IProductService iProductService; 
		@Autowired 
		private IFileService iFileService; 
   
	//更新商品或保存
	@RequestMapping("save.do")
	@ResponseBody
	public ServerResponse productSave(HttpSession session,Product product){
		
		User user= (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员！");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			//增加产品的业务逻辑
			return  iProductService.saveOrUpdate(product);
		}else{
			return ServerResponse.createByErrorMessage("需要管理员权限，无权限操作！");
		}
	}
	
	//设置销售数据
	@RequestMapping("set_sale_status.do")
	@ResponseBody
	public ServerResponse setSaleStatus(HttpSession session,Integer productId,Integer status){
		
		User user= (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员！");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			//增加产品的业务逻辑
			return  iProductService.setSaleStatus(productId, status);
		}else{
			return ServerResponse.createByErrorMessage("需要管理员权限，无权限操作！");
		}
	}
	
	
	@RequestMapping("detail.do")
	@ResponseBody
	public ServerResponse getDetail(HttpSession session,Integer productId){
		
		User user= (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员！");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			//增加细节业务逻辑
			return iProductService.manageProductDetail(productId);	
		
		}else{
			return ServerResponse.createByErrorMessage("需要管理员权限，无权限操作！");
		}
	}
	
	
	@RequestMapping("list.do")
	@ResponseBody
	public ServerResponse getList(HttpSession session,@RequestParam(value="pageNum",defaultValue="1")int pageNum,
			@RequestParam(value="pagSize",defaultValue="10")int pageSize) {
		User user= (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员！");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			//增加细节业务逻辑
			return iProductService.getProductList(pageNum,pageSize);
			
		}else{
			return ServerResponse.createByErrorMessage("需要管理员权限，无权限操作！");
		}
	}
	
	@RequestMapping("search.do")
	@ResponseBody
	public ServerResponse<PageInfo> productSearch(HttpSession session,
								@RequestParam(value="productName",required=false)String productName,
								@RequestParam(value="productId",required=false)Integer productId,
								@RequestParam(value="pageNum",defaultValue="1")int pageNum,
								@RequestParam(value="pageSize",defaultValue="10")int pageSize) {
		User user= (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "搜索失败！用户未登录，请登录管理员！");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			//增加细节业务逻辑
			return iProductService.searchProduct(productName, productId, pageNum, pageSize);
		}else{
			return ServerResponse.createByErrorMessage("需要管理员权限，无权限操作！");
		}
	}
	
	//文件的上传controller
	@RequestMapping("upload.do")
	@ResponseBody
	//MultipartFile的value必须与上传文件的name一样
    public ServerResponse upload(HttpSession session,@RequestParam(value="upload_file",required=false)MultipartFile file,
    						HttpServletRequest request){
		User user= (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录管理员！");
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			//增加细节业务逻辑
			String path=request.getSession().getServletContext().getRealPath("upload");
			String targetFileName=iFileService.upload(file, path);
			String url=PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
			Map fileMap=Maps.newHashMap();
			fileMap.put("uri", targetFileName);
			fileMap.put("url", url);
			return ServerResponse.createBySuccess(fileMap);
		}else{
			return ServerResponse.createByErrorMessage("需要管理员权限，无权限操作！");
		}
	}	
	
	//富文本的上传controller
	@RequestMapping("richtext_img_upload.do")
	@ResponseBody
	//MultipartFile的value必须与上传文件的name一样
	public Map richtextImgUpload(HttpSession session,@RequestParam(value="upload_file",required=false)MultipartFile file,HttpServletRequest request,
										HttpServletResponse response){
			Map resultMap = Maps.newHashMap();
		User user= (User) session.getAttribute(Const.CURRENT_USER);
		if(user==null){
			resultMap.put("success", false);
			resultMap.put("msg", "请登录管理员！");
				return resultMap;
		}
		if(iUserService.checkAdminRole(user).isSuccess()){
			//增加细节业务逻辑
			//富文本对于返回值有自己的要求 ，使用 的simditor所以按照simditor的要求进行返回。
			String path=request.getSession().getServletContext().getRealPath("upload");
			String targetFileName=iFileService.upload(file, path);
			 //判断targetfilename
			if(StringUtils.isBlank(targetFileName)){
				resultMap.put("success", false);
				resultMap.put("msg", "上传失败！");
					return resultMap;
			}
			
			String url=PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
			resultMap.put("success", true);
			resultMap.put("msg", "上传成功！！");
			resultMap.put("file_path",url);
				return resultMap;
		}else{
			resultMap.put("success", false);
			resultMap.put("msg", "无权限操作！");
				return resultMap;
		}
	}	
	
	
	
	
}
