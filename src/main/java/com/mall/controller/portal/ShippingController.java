package com.mall.controller.portal;

import javax.servlet.http.HttpSession;
import javax.xml.ws.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.Shipping;
import com.mall.pojo.User;
import com.mall.service.IShippingService;

@Controller
@RequestMapping("/shipping/")
public class ShippingController {

	@Autowired
	private IShippingService iShippingService;
//添加地址	
@RequestMapping("add.do")
@ResponseBody
public ServerResponse add(HttpSession session,Shipping shipping){
	User user=(User) session.getAttribute(Const.CURRENT_USER);
	if(user==null){
		return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
	}
	return iShippingService.add(user.getId(), shipping);
}

//更新操作
@RequestMapping("del.do")
@ResponseBody
public ServerResponse del(HttpSession session, Integer shippingId){
	User user=(User) session.getAttribute(Const.CURRENT_USER);
	if(user==null){
		return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
	}
	return iShippingService.del(user.getId(), shippingId);
}

@RequestMapping("update.do")
@ResponseBody
public ServerResponse update(HttpSession session, Shipping shipping){
	User user=(User) session.getAttribute(Const.CURRENT_USER);
	if(user==null){
		return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
	}
	return iShippingService.update(user.getId(),shipping);
}

@RequestMapping("select.do")
@ResponseBody
public ServerResponse select(HttpSession session, Integer shippingId){
	User user=(User) session.getAttribute(Const.CURRENT_USER);
	if(user==null){
		return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
	}
	return iShippingService.select(user.getId(),shippingId);
}

public ServerResponse<PageInfo> list(@RequestParam(value="pageNum",defaultValue="1")int pageNum,
									@RequestParam(value="pageSize",defaultValue="10")int pageSize,
									HttpSession session){
	User user=(User) session.getAttribute(Const.CURRENT_USER);
	if(user==null){
		return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
	}
	return iShippingService.list(user.getId(),pageNum,pageSize);
}



}
