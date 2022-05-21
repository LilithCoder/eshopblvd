package com.hatsukoi.eshopblvd.api.cart;

import com.hatsukoi.eshopblvd.vo.OrderItemVO;

import java.util.HashMap;
import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/05/09 Mon 9:08 PM
 */
public interface CartService {
    /**
     * 获取当前用户购物车的购物项列表（OrderItemVO）
     * @param userId
     * @return
     */
    HashMap<String, Object> getUserCartItems(Long userId);
}
