package com.mall.controller.portal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.User;
import com.mall.service.IUserService;

/**
 * @author wenjiewang
 *  用户控制
 */
@Controller
@RequestMapping("/user/")
public class UserController {
			
	
	@Autowired
	private IUserService iUserService;
	
	/**
	 * 用户登录
	 * 
	 */
	@RequestMapping(value="login.do",method=RequestMethod.POST)
	@ResponseBody
   public ServerResponse<User> login(String username,String password,HttpSession session){
	   //调用Service-->mybatis-->dao
		ServerResponse<User> response=iUserService.login(username, password);
		if(response.isSuccess()){
			session.setAttribute(Const.CURRENT_USER, response.getData());
		}
	   return response;
   }
	/**
	 * 退出登录
	 */
	@RequestMapping(value="logout.do",method=RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> logout(HttpSession session){
		session.removeAttribute(Const.CURRENT_USER);
		return ServerResponse.createBySuccess();
	}
	/**
	 * 注册
	 */
	@RequestMapping(value="register.do",method=RequestMethod.POST)
		@ResponseBody
	public ServerResponse<String> register(User user){
		System.out.println(user.toString());
		return iUserService.register(user);
	}
	//用户或者邮箱是否存在  校验
	@RequestMapping(value="check_valid.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> checkValid(String str,String type){
			return iUserService.checkValid(str, type);
	}
	@RequestMapping(value="get_User_Info.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpSession session){
		User user=(User)session.getAttribute(Const.CURRENT_USER);
		if(user!=null){
			return ServerResponse.createBySuccess(user);
		}
		return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息！");
	}
	
	@RequestMapping(value="forget_Get_Question.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetGetQuestion(String username){
		return iUserService.selectQuestion(username);
		
	}
	//校验问题 密码
	@RequestMapping(value="forget_Check_Answer.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
		return iUserService.checkAnswer(username,question,answer);
		
	}
	@RequestMapping(value="forget_Rest_Password.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
		return iUserService.forgetRestPassword(username, passwordNew, forgetToken);
	}
	//忘记密码中的重置密码 
	@RequestMapping(value="reset_Password.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
			User user=(User) session.getAttribute(Const.CURRENT_USER);
			if(user == null){
				return ServerResponse.createByErrorMessage("用户未登录！;");
			}
			return iUserService.resetPassword(passwordOld, passwordNew,user);
	}
	
	//更新个人用户信息 
	@RequestMapping(value="update_information.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> updateinformation(HttpSession session,User user){
		User currentuser=(User) session.getAttribute(Const.CURRENT_USER);
		if(currentuser == null){
			return ServerResponse.createByErrorMessage("用户未登录！;");
		}
	    user.setId(currentuser.getId());
	    user.setUsername(currentuser.getUsername());
	   ServerResponse<User> response= iUserService.updateInformation(user);
	   if(response.isSuccess()){
		   session.setAttribute(Const.CURRENT_USER, response.getData());
	   }
	   return response;
	}
	@RequestMapping(value="get_information.do",method=RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> get_information(HttpSession session){
		User currentuser=(User) session.getAttribute(Const.CURRENT_USER);
		if(currentuser == null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录,需要强制登录！status=10");
		}
		return iUserService.getInformation(currentuser.getId());
	}
}
