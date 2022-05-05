package com.hatsukoi.eshopblvd.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hatsukoi.eshopblvd.api.product.ProductRpcService;
import com.hatsukoi.eshopblvd.cart.constant.CartConstant;
import com.hatsukoi.eshopblvd.cart.interceptor.CartInterceptor;
import com.hatsukoi.eshopblvd.cart.service.CartService;
import com.hatsukoi.eshopblvd.cart.to.UserInfoTO;
import com.hatsukoi.eshopblvd.cart.vo.CartItemVO;
import com.hatsukoi.eshopblvd.to.SkuInfoTO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author gaoweilin
 * @date 2022/05/05 Thu 12:59 PM
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ThreadPoolExecutor executor;

    @Reference(interfaceName = "com.hatsukoi.eshopblvd.api.product.ProductRpcService", check = false)
    private ProductRpcService productRpcService;

    /**
     * 商品sku加入购物车
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public CartItemVO addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        // 1. 根据userId来获取用户购物车，或者根据userKey获取离线购物车
        BoundHashOperations<String, Object, Object> cartOp = getCartOp();

        // 2. 获取到购物车后，查找购物车中有没有skuId对应的商品，购物车的数据结构为hash，key为skuId
        String jsonStr = (String) cartOp.get(skuId.toString());
        if (StringUtils.isEmpty(jsonStr)) {
            // 2.1 如果没有，异步查询sku的需要相关信息，存入redis
            CartItemVO cartItem = new CartItemVO();

            // 2.1.1 RPC调用商品服务查询skuInfo基本信息
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                CommonResponse resp = CommonResponse.convertToResp(productRpcService.getSkuInfo(skuId));
                SkuInfoTO data = resp.getData(new TypeReference<SkuInfoTO>() {
                });
                cartItem.setSkuId(data.getSkuId());
                cartItem.setCheck(true);
                cartItem.setTitle(data.getSkuTitle());
                cartItem.setImage(data.getSkuDefaultImg());
                cartItem.setPrice(data.getPrice());
                cartItem.setCount(num);
            }, executor);

            // 2.1.2 RPC调用商品服务查询sku的销售属性
            CompletableFuture<Void> getSkuSaleAttrsWithValueTask = CompletableFuture.runAsync(() -> {
                CommonResponse resp = CommonResponse.convertToResp(productRpcService.getSkuSaleAttrsWithValue(skuId));
                List<String> data = resp.getData(new TypeReference<List<String>>() {
                });
                cartItem.setSkuAttr(data);
            }, executor);

            // 2.1.3 等异步操作都结束后将购物车项放入redis
            CompletableFuture.allOf(getSkuInfoTask, getSkuSaleAttrsWithValueTask).get();
            cartOp.put(skuId.toString(), JSON.toJSONString(cartItem));

            return cartItem;
        } else {
            // 2.2 如果有，将查找到的这项商品购物车项中数量累加一下，继续放入redis中
            CartItemVO cartItem = JSON.parseObject(jsonStr, CartItemVO.class);
            cartItem.setCount(cartItem.getCount() + num);
            cartOp.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        }
    }

    /**
     * 获取当前要操作购物车的redis操作
     * 优先获取操作在线购物车，没有用户登陆了才获取操作离线购物车
     */
    private BoundHashOperations<String, Object, Object> getCartOp() {
        UserInfoTO userInfoTO = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTO.getUserId() != null) {
            // 有用户登陆, 要获取在线购物车，key为eshopblvd:cart:{userId}
            cartKey = CartConstant.CART_PREFIX + userInfoTO.getUserId();
        } else {
            // 有用户登陆, 要获取离线购物车，key为eshopblvd:cart:{userKey}
            cartKey = CartConstant.CART_PREFIX + userInfoTO.getUserKey();
        }
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }
}
