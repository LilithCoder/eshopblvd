package com.hatsukoi.eshopblvd.cart.service;

import com.hatsukoi.eshopblvd.cart.vo.CartItemVO;
import com.hatsukoi.eshopblvd.cart.vo.CartVO;

import java.util.concurrent.ExecutionException;

/**
 * @author gaoweilin
 * @date 2022/05/05 Thu 12:59 PM
 */
public interface CartService {
    CartItemVO addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartVO getCart() throws ExecutionException, InterruptedException;

    void checkCartItem(Long skuId, Boolean checked);

    void changeItemCount(Long skuId, Integer num);

    void deleteItem(Long skuId);
}
