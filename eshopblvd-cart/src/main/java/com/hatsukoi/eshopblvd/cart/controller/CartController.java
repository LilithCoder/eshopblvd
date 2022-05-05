package com.hatsukoi.eshopblvd.cart.controller;

import com.hatsukoi.eshopblvd.cart.service.CartService;
import com.hatsukoi.eshopblvd.cart.vo.CartItemVO;
import com.hatsukoi.eshopblvd.cart.vo.CartVO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author gaoweilin
 * @date 2022/05/05 Thu 1:37 AM
 */
@RestController
@RequestMapping("cart")
public class CartController {
    @Autowired
    private CartService cartService;

    /**
     * TODO: 接口幂等性质，防止重复提交的问题
     * 将sku加入购物车
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/addToCart")
    public CommonResponse addToCart(@RequestParam("skuId") Long skuId,
                                    @RequestParam("num") Integer num) throws ExecutionException, InterruptedException {
        CartItemVO item = cartService.addToCart(skuId, num);
        return CommonResponse.success().setData(item);
    }

    @GetMapping("/getCart")
    public CommonResponse getCart() throws ExecutionException, InterruptedException {
        CartVO cart = cartService.getCart();
        return CommonResponse.success().setData(cart);
    }
}
