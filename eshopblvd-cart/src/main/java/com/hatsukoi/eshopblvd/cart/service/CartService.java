package com.hatsukoi.eshopblvd.cart.service;

import com.hatsukoi.eshopblvd.cart.vo.CartItemVO;

import java.util.concurrent.ExecutionException;

/**
 * @author gaoweilin
 * @date 2022/05/05 Thu 12:59 PM
 */
public interface CartService {
    CartItemVO addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;
}
