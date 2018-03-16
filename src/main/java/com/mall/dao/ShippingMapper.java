package com.mall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mall.pojo.Shipping;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);
    
    int deleteByshippingIdUserId(@Param("userid") int userid,@Param("shippingId") int shippingId);

	int updateByshipping(Shipping record);
	
	Shipping selectByShippingIdUserId(@Param("userid") int userid,@Param("shippingId") int shippingId);
	  
	List<Shipping> selectByUserId(@Param("userid") int userid);
}