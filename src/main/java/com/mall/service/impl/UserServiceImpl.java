package com.mall.service.impl;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.common.TokenCache;
import com.mall.dao.UserMapper;
import com.mall.pojo.User;
import com.mall.service.IUserService;
import com.mall.util.MD5Util;


@Service("iUserService")
public class UserServiceImpl implements IUserService {
    
	@Autowired  
	private UserMapper userMapper;
	/**
	 * 用户登录
	 */
	@Override
	public ServerResponse<User> login(String username, String password) {
		int resultCount =userMapper.checkUsername(username);
		if(resultCount == 0){
			return ServerResponse.createByErrorMessage("用户名不存在！");
		}
		//todo  密码登录MD5
		//使用MD5加密密码
		String MD5password =MD5Util.MD5EncodeUtf8(password);
	    User user=userMapper.selectLogin(username, MD5password);
	    if(user == null){
	    	return ServerResponse.createByErrorMessage("密码错误！");
	    }
	    user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
	    return ServerResponse.createBySuccess("登陆成功！", user);
	}
	
	//用户注册
     public ServerResponse<String> register(User user){
    	 ServerResponse<String> validResponse = this.checkValid(user.getUsername(),Const.USERNAME);
         if(!validResponse.isSuccess()){
             return validResponse;
         }
         validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
         if(!validResponse.isSuccess()){
               return validResponse;
         }
         System.out.println("impl"+user.toString());
		user.setRole(Const.Role.ROLE_CUSTOMER);
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        System.out.println("impl"+user.toString());
        	return ServerResponse.createBySuccessMessage("注册成功");
    }
     
     
     
       public ServerResponse<String> checkValid(String str, String type){
           //如果不为空  开始校验       
    	   if(StringUtils.isNotBlank(type)){
    		   if(Const.USERNAME.equals(type)){
    			   		int resultCount =userMapper.checkUsername(str);
    			   			if(resultCount > 0){
    			   					return ServerResponse.createByErrorMessage("用户名已存在！");
    			   }
    		   }
    		   if(Const.EMAIL.equals(type)){
    			   		int resultCount = userMapper.checkEmail(str);
    			   			if(resultCount > 0){
    			   				return ServerResponse.createByErrorMessage("email已存在！");
    				} 
    		   }
            }else{
                return 	ServerResponse.createByErrorMessage("参数错误 ！");  
                  }
    	   return 	ServerResponse.createBySuccessMessage("校验成功 ！");   
       }
       //忘记密码，验证问题。
       public ServerResponse selectQuestion(String username){
    	   ServerResponse validResponse=this.checkValid(username, Const.USERNAME);
    	   	if(validResponse.isSuccess()){
    	   		//用户不存在。。
    	   		return ServerResponse.createByErrorMessage("用户不存在！");
    	   	}
    	   String question=userMapper.selectQuestionByUsername(username);
    	   if(StringUtils.isNotBlank(question)){
    		   return ServerResponse.createBySuccess(question);
    	   }
    	   return ServerResponse.createByErrorMessage("找回密码问题为空！");
    	   
       }
	
	public ServerResponse<String> checkAnswer(String username, String question, String answer) {
		int resultCount=userMapper.checkAnswer(username, question, answer);
		if(resultCount>0){
			//说明问题答案都正确
		    //声明一个token
			String forgetToken=UUID.randomUUID().toString();
			TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, forgetToken); 
				return	ServerResponse.createBySuccessMessage(forgetToken);
		}
		return ServerResponse.createByErrorMessage("问题答案错误 ！");
	}
      
    //重置密码
	public ServerResponse<String> forgetRestPassword(String username,String passwordNew,String forgetToken){
		if(StringUtils.isNotBlank(forgetToken)){
			return ServerResponse.createByErrorMessage("参数错位，Token需要传递！");
		}
		ServerResponse validResponse=this.checkValid(username, Const.USERNAME);
	   	if(validResponse.isSuccess()){
	   		//用户不存在。。
	   		return ServerResponse.createByErrorMessage("用户不存在！");
	   	}
		String token=TokenCache.getKey(TokenCache.TOKEN_PREFIX+username); 
		if(StringUtils.isNotBlank(forgetToken)){
			return ServerResponse.createByErrorMessage("Token无效或过期！");
		}
		if(StringUtils.equals(forgetToken, token)){
			String md5Password =MD5Util.MD5EncodeUtf8(passwordNew);
			int rowcount=userMapper.updatePasswordByUsername(username, md5Password);
				if(rowcount > 0){
			    return ServerResponse.createBySuccessMessage("密码修改成功！");
				}	
		}else{
			return ServerResponse.createByErrorMessage("Token错误，请重新获取重置密码的token！");
		}
		return ServerResponse.createByErrorMessage("修改密码失败！");
	}
     //已登陆  重置密码
	public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
		//为防止横向越权  需要再校验一下密码，
		int resultCount= userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
		if(resultCount == 0){
			return ServerResponse.createByErrorMessage("旧密码错误！");
		}
		user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
		int updateCount=userMapper.updateByPrimaryKeySelective(user);
		if(updateCount>0){
			return ServerResponse.createBySuccessMessage("密码修改成功！");
		}
		return ServerResponse.createByErrorMessage("密码更新失败！");
	}
    public ServerResponse<User> updateInformation(User user){
    	//username 不能被更新 
    	//需要校验email
    	//email不能存在，并且不能 与旧email相同。
    		int resultCount =userMapper.checkEmailByUserId(user.getEmail(),user.getId());
    		if(resultCount>0){
    			return ServerResponse.createByErrorMessage("email已经存在，请更换再次尝试！");
    		}
    		User updateUser = new User();
    		updateUser.setId(user.getId());
    		updateUser.setEmail(user.getEmail());
    		updateUser.setPhone(user.getPhone());
    		updateUser.setQuestion(user.getQuestion());
    		updateUser.setAnswer(user.getAnswer());
    		
    		int updateCount =userMapper.updateByPrimaryKey(updateUser);
    		
    		if(updateCount>0){
    			return ServerResponse.createBySuccess("信息更新成功 ！",updateUser);
    		}
    		return ServerResponse.createByErrorMessage("更新信息失败！");
    }  
	//获取个人信息 
    public ServerResponse<User> getInformation(Integer userId){
    	User user=userMapper.selectByPrimaryKey(userId);
    	if(user == null){
    		return  ServerResponse.createByErrorMessage("找不到当前用户！");
    	}
    	user.setPassword(user.getPassword());
    	return ServerResponse.createBySuccess(user);
    }
    
    //校验是否管理员		
      public ServerResponse checkAdminRole(User user){
    	  if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
    		  return ServerResponse.createBySuccess();
    	  }
    	  return ServerResponse.createByError();
      }
    /**
     * 商品管理。
     * 
     */
      
      
      
      
      
      
      
      
}
