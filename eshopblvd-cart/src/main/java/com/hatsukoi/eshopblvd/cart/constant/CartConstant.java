package com.hatsukoi.eshopblvd.cart.constant;

/**
 * @author gaoweilin
 * @date 2022/05/05 Thu 10:50 AM
 */
public class CartConstant {
    /**
     * 临时用户cookie的key
     */
    public static final String TEMP_USER_COOKIE_NAME = "user-key";
    /**
     * 临时用户cookie过期时间 （30天）
     */
    public static final int TEMP_USER_COOKIE_TIMEOUT = 60*60*24*30;
    /**
     * 购物车key的前缀
     */
    public static final String CART_PREFIX = "eshopblvd:cart:";
}
