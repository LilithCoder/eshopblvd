package com.hatsukoi.eshopblvd.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hatsukoi.eshopblvd.api.cart.CartService;
import com.hatsukoi.eshopblvd.api.product.ProductRpcService;
import com.hatsukoi.eshopblvd.cart.constant.CartConstant;
import com.hatsukoi.eshopblvd.cart.vo.CartItemVO;
import com.hatsukoi.eshopblvd.to.SkuHasStockVO;
import com.hatsukoi.eshopblvd.to.SkuPriceTO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import com.hatsukoi.eshopblvd.vo.OrderItemVO;
import com.hatsukoi.eshopblvd.ware.service.WareSkuRPCService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/05/09 Mon 9:08 PM
 */
@Slf4j
@Service
@org.apache.dubbo.config.annotation.Service
public class CartRpcServiceImpl implements CartService {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Reference(interfaceName = "com.hatsukoi.eshopblvd.api.product.ProductRpcService", check = false)
    ProductRpcService productRpcService;

    @Reference(check = false, interfaceName = "com.hatsukoi.eshopblvd.ware.service.WareSkuRPCService")
    WareSkuRPCService wareSkuRPCService;

    /**
     * 获取当前用户购物车的购物项列表（封装成order项）
     * @param userId
     * @return
     */
    @Override
    public CommonResponse getUserCartItems(Long userId) {
        // TODO: 搞个拦截器通过传来的sessionId获取登录态
        if (userId != null) {
            String cartKey = CartConstant.CART_PREFIX + userId;
            // 获取所有购物项
            List<CartItemVO> cartItems = getCartItems(cartKey);
            // 获取所有购物项的skuId
            List<Long> skuIds = cartItems.stream().filter(item -> {
                return item.getCheck();
            }).map(item -> {
                return item.getSkuId();
            }).collect(Collectors.toList());

            // RPC调用获取skuId->价格映射
            CommonResponse resp = CommonResponse.convertToResp(productRpcService.getSkusPrice(skuIds));
            List<SkuPriceTO> data = resp.getData(new TypeReference<List<SkuPriceTO>>() {
            });
            Map<Long, BigDecimal> priceMap = data.stream().collect(Collectors.toMap(SkuPriceTO::getSkuId, SkuPriceTO::getPrice));

            // RPC调用获取skuId->是否有库存
            Map<Long, Boolean> stockMap = null;
            try {
                CommonResponse response = CommonResponse.convertToResp(wareSkuRPCService.getSkusHasStock(skuIds));
                List<SkuHasStockVO> skuHasStockList = response.getData(new TypeReference<List<SkuHasStockVO>>(){});
                stockMap = skuHasStockList.stream().collect(Collectors.toMap(SkuHasStockVO::getSkuId, SkuHasStockVO::getHasStock));
            } catch (Exception e) {
                log.error("【RPC调用】获取sku库存信息错误：{}", e);
            }

            // 最后封装数据
            Map<Long, Boolean> finalStockMap = stockMap;
            List<OrderItemVO> collect = cartItems.stream().map(item -> {
                OrderItemVO orderItemVO = new OrderItemVO();
                BeanUtils.copyProperties(item, orderItemVO);
                // 更新最新的价格
                orderItemVO.setPrice(priceMap.get(item.getSkuId()));
                // 更新sku的库存状况
                orderItemVO.setHasStock(finalStockMap.get(item.getSkuId()));
                return orderItemVO;
            }).collect(Collectors.toList());
            return CommonResponse.success().setData(collect);
        } else {
            return CommonResponse.error();
        }
    }

    private List<CartItemVO> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if (values != null && values.size() > 0){
            List<CartItemVO> collect = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItemVO cartItem = JSON.parseObject(str, CartItemVO.class);
                // 只返回被选中的购物项
                if (cartItem.getCheck()) {
                    return cartItem;
                } else {
                    return null;
                }
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }
}
