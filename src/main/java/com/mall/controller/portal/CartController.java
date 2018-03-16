package com.mall.controller.portal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.ICartService;
import com.mall.vo.CartVo;

@Controller
@RequestMapping("/cart/")
public class CartController {
   
	@Autowired
	private ICartService iCartService;
	
	//添加到购物车
@RequestMapping("list.do")
@ResponseBody
public ServerResponse<CartVo> list(HttpSession session){
		User user=(User) session.getAttribute(Const.CURRENT_USER);
		if(user == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		return iCartService.list(user.getId());
	}
//传入购物的物品ID和需要购买的数量
@RequestMapping("add.do")
@ResponseBody
public ServerResponse<CartVo> add(HttpSession session,Integer count,Integer productId){
	User user=(User) session.getAttribute(Const.CURRENT_USER);
	if(user == null){
		return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
	}
	return iCartService.add(user.getId(), productId, count);
}
//更新购物的数量和购买的物品的id
@RequestMapping("update.do")
@ResponseBody
public ServerResponse<CartVo> update(HttpSession session,Integer count,Integer productId){
	User user=(User) session.getAttribute(Const.CURRENT_USER);
	if(user == null){
		return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
	}
	return iCartService.update(user.getId(), productId, count);
}

@RequestMapping("delete_product.do")
@ResponseBody
public ServerResponse<CartVo> deleteProduct(HttpSession session,String productIds){
	User user=(User) session.getAttribute(Const.CURRENT_USER);
	if(user == null){
		return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
	}
	return iCartService.deleteProduct(user.getId(), productIds);
}

/**
 * 全选	
 * 反选
 * 
 * 单独选
 * 单独反选
 * 
 * 查询当前用户购物车里面的产品数量 ， 如果一个产品有10个，那么数量就显示10
 */
@RequestMapping("select_all.do")
@ResponseBody
public ServerResponse<CartVo> selectAll(HttpSession session,Integer count,Integer productId){
	User user=(User) session.getAttribute(Const.CURRENT_USER);
	if(user == null){
		return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
	}
	return iCartService.selectOrUnselect(user.getId(), null,Const.Cart.CHECKED);
}


@RequestMapping("un_select_all.do")
@ResponseBody
public ServerResponse<CartVo> unSelectAll(HttpSession session,Integer count,Integer productId){
	User user=(User) session.getAttribute(Const.CURRENT_USER);
	if(user == null){
		return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
	}
	return iCartService.selectOrUnselect(user.getId(),null, Const.Cart.UN_CHECKED);
}

@RequestMapping("un_select.do")
@ResponseBody
public ServerResponse<CartVo> unSelect(HttpSession session,Integer count,Integer productId){
	User user=(User) session.getAttribute(Const.CURRENT_USER);
	if(user == null){
		return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
	}
	return iCartService.selectOrUnselect(user.getId(),null, Const.Cart.UN_CHECKED);
}

@RequestMapping("select.do")
@ResponseBody
public ServerResponse<CartVo> select(HttpSession session,Integer count,Integer productId){
	User user=(User) session.getAttribute(Const.CURRENT_USER);
	if(user == null){
		return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
	}
	return iCartService.selectOrUnselect(user.getId(),null, Const.Cart.CHECKED);
}
//获取当前购物车中的物品数量
@RequestMapping("get_cart_product_count.do")
@ResponseBody
public ServerResponse<Integer> getCartProductCount(HttpSession session){
	User user  =(User)session.getAttribute(Const.CURRENT_USER);
	if(user == null){
			return ServerResponse.createBySuccess(0);
	}
	return iCartService.getCartProductCount(user.getId());
}

}
