package com.mall.test;

import java.math.BigDecimal;

import org.junit.Test;

public class BigDecimalTest {
   @Test
   public void test1() {
	   BigDecimal a =new BigDecimal("0.05");
	   BigDecimal b =new BigDecimal("0.01");
	   System.out.println(a.add(b));
}
}
