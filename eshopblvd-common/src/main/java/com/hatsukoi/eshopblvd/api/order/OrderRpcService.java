package com.hatsukoi.eshopblvd.api.order;

import java.util.HashMap;

/**
 * @author gaoweilin
 * @date 2022/05/20 Fri 3:08 AM
 */
public interface OrderRpcService {
    HashMap<String, Object> getOrderStatus(String orderSn);
}
