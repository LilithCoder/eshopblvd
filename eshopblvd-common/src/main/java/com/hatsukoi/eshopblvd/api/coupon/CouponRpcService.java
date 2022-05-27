package com.hatsukoi.eshopblvd.api.coupon;

import java.util.HashMap;

/**
 * @author gaoweilin
 * @date 2022/05/27 Fri 10:48 AM
 */
public interface CouponRpcService {
    HashMap<String, Object> scanNext3DaysSeckillSession();
}
