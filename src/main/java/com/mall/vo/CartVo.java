package com.mall.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartVo {
   //包含一个CartProductVo的集合
		private  List<CartProductVo> cartProductVoList;
		private BigDecimal cartTotalPrice;
		private Boolean allChecked;
		private String imageHost;
		
		
		public List<CartProductVo> getCartProductVoList() {
			return cartProductVoList;
		}
		public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
			this.cartProductVoList = cartProductVoList;
		}
		public BigDecimal getCartTotalPrice() {
			return cartTotalPrice;
		}
		public void setCartTotalPrice(BigDecimal cartTotalPrice) {
			this.cartTotalPrice = cartTotalPrice;
		}
		public Boolean getAllChecked() {
			return allChecked;
		}
		public void setAllChecked(Boolean allChecked) {
			this.allChecked = allChecked;
		}
		public String getImageHost() {
			return imageHost;
		}
		public void setImageHost(String imageHost) {
			this.imageHost = imageHost;
		}
		
		
	
}
