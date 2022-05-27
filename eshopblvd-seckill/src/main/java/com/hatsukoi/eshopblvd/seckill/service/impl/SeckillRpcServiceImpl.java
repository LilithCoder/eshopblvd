package com.hatsukoi.eshopblvd.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.hatsukoi.eshopblvd.api.seckill.SeckillRpcService;
import com.hatsukoi.eshopblvd.to.SeckillSkuRedisTo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author gaoweilin
 * @date 2022/05/27 Fri 3:56 PM
 */
@Service
@com.alibaba.dubbo.config.annotation.Service
public class SeckillRpcServiceImpl implements SeckillRpcService {
    private final String SKUKILL_CACHE_KEY = "seckill:skus";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public HashMap<String, Object> getSkuSeckillInfo(Long skuId) {
        // 找到所有需要参与秒杀的商品的key
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_KEY);
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            String regx = "\\d_" + skuId; // 找到目标sku
            for (String key : keys) {
                if (Pattern.matches(regx, key)) {
                    String jsonStr = hashOps.get(key);
                    SeckillSkuRedisTo skuRedisTo = JSON.parseObject(jsonStr, SeckillSkuRedisTo.class);
                    if (skuRedisTo != null) {
                        long now = new Date().getTime();
                        if (now < skuRedisTo.getStartTime() || now > skuRedisTo.getEndTime()) {
                            // 商品过期了，直接删除
                            hashOps.delete(key);
                            // 过期了就不返回随机码
                            skuRedisTo.setRandomCode(null);
                        }
                        return CommonResponse.success().setData(skuRedisTo);
                    }
                }
            }
        }
        return CommonResponse.error();
    }
}
