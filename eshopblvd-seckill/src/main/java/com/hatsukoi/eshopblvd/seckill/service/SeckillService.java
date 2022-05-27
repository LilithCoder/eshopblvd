package com.hatsukoi.eshopblvd.seckill.service;

import com.hatsukoi.eshopblvd.seckill.to.SeckillSkuRedisTo;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/05/27 Fri 10:40 AM
 */
public interface SeckillService {
    void uploadSeckillNext3Days();

    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    String kill(String killId, String key, Integer num);
}
