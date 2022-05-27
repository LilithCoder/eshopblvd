package com.hatsukoi.eshopblvd.api.seckill;

import java.util.HashMap;

/**
 * @author gaoweilin
 * @date 2022/05/27 Fri 3:56 PM
 */
public interface SeckillRpcService {
    HashMap<String, Object> getSkuSeckillInfo(Long skuId);
}
