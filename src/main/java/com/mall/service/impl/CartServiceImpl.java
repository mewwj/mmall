package com.mall.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.dao.CartMapper;
import com.mall.dao.ProductMapper;
import com.mall.pojo.Cart;
import com.mall.pojo.Product;
import com.mall.service.ICartService;
import com.mall.util.BigDecimalUtil;
import com.mall.util.PropertiesUtil;
import com.mall.vo.CartProductVo;
import com.mall.vo.CartVo;


@Service("iCartService")
public class CartServiceImpl implements ICartService {
		@Autowired 
		private CartMapper cartMapper;
		
		@Autowired 
		private ProductMapper productMapper;
		
		
	public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){
		if(productId == null ||count==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		   Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
		if(cart==null){
			//这个产品不在购物车里面 
			Cart cartItem = new Cart();
			cartItem.setQuantity(count);
			cartItem.setChecked(Const.Cart.CHECKED);
			cartItem.setProductId(productId);
			cartItem.setUserId(userId);
			cartMapper.insert(cartItem);
		}else{
			//这个产品已经在购物车里面了
			//如果已经存在，数量相加
		 count = cart.getQuantity() + count;
			cart.setQuantity(count);
			cartMapper.updateByPrimaryKeySelective(cart);
		}
		return this.list(userId);
	}
	
	public  ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count){
		if(productId == null ||count==null){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Cart cart=cartMapper.selectCartByUserIdProductId(userId, productId);
		if(cart!=null){
			cart.setQuantity(count);
		}
		cartMapper.updateByPrimaryKeySelective(cart);
		return this.list(userId);
	}
	/**
	 * 删除商品
	 */
	public  ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
		List<String> productList = Splitter.on(",").splitToList(productIds);
		if(CollectionUtils.isEmpty(productList)){
			return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
 		cartMapper.deleteByUserIdProductIds(userId, productList);
 		return this.list(userId);
	}
	/**
	 *  搜索 
	 */
public ServerResponse<CartVo> list (Integer userId){
	CartVo cartVo = this.getCartVoLimit(userId);
	return ServerResponse.createBySuccess(cartVo);
}	
	
	
public ServerResponse<CartVo> selectOrUnselect(Integer userId,Integer productId,Integer checked) {
	cartMapper.checkOrUncheckedProduct(userId, productId, checked);
	return this.list(userId);
}	

public ServerResponse<Integer> getCartProductCount(Integer userId){
	if(userId == null){
		return ServerResponse.createBySuccess(0);
	}
	return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
}






	private CartVo getCartVoLimit(Integer userId){
		CartVo cartVo=new CartVo();
		List<Cart> cartList =cartMapper.selectCartByUserId(userId);
		List<CartProductVo> cartProductVoList =Lists.newArrayList();

		BigDecimal cartTotalPrice = new BigDecimal("0");
		
		if(CollectionUtils.isNotEmpty(cartList)){
			for(Cart cartItem : cartList){
				CartProductVo  cartProductVo =new CartProductVo();
				cartProductVo.setId(cartItem.getId());
				cartProductVo.setUserId(userId);
				cartProductVo.setProductId(cartItem.getProductId());
				Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
				if(product != null){
					cartProductVo.setPaoductMainImage(product.getMainImage());
					cartProductVo.setProductName(product.getName());
					cartProductVo.setProductSubtitle(product.getSubtitle());
					cartProductVo.setProductStatus(product.getStatus());
					cartProductVo.setProductPrice(product.getPrice());
					cartProductVo.setProductStock(product.getStock());
					int buyLimitCount = 0;
					if(product.getStock() >= cartItem.getQuantity()){
						buyLimitCount = cartItem.getQuantity();
						cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
					}else{
						buyLimitCount = product.getStock();
						cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
						Cart cartForQuantity =new Cart();
						cartForQuantity.setId(cartItem.getId());
						cartForQuantity.setQuantity(buyLimitCount);
						cartMapper.updateByPrimaryKeySelective(cartForQuantity);
					}
					cartProductVo.setQuantity(buyLimitCount);
					cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
					cartProductVo.setProductChecked(cartItem.getChecked());
				}
				
				if(cartItem.getChecked() == Const.Cart.CHECKED){
					cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
				}
				cartProductVoList.add(cartProductVo);
			}
		}
		cartVo.setCartTotalPrice(cartTotalPrice);
		cartVo.setCartProductVoList(cartProductVoList);
		cartVo.setAllChecked(this.getAllCheckedStatus(userId));
		cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
		return cartVo;
	}
	
	
	private boolean getAllCheckedStatus(Integer userId){
		if(userId == null){
			return false;
		}
		return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
	}
	
	
	
	
	
	
	
}
