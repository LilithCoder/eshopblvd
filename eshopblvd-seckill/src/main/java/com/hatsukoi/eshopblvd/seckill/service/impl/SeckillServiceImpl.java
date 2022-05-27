package com.hatsukoi.eshopblvd.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hatsukoi.eshopblvd.api.coupon.CouponRpcService;
import com.hatsukoi.eshopblvd.api.product.ProductRpcService;
import com.hatsukoi.eshopblvd.seckill.service.SeckillService;
import com.hatsukoi.eshopblvd.seckill.to.SeckillSkuRedisTo;
import com.hatsukoi.eshopblvd.to.SeckillSessionTo;
import com.hatsukoi.eshopblvd.to.SeckillSkuRelationTo;
import com.hatsukoi.eshopblvd.to.SkuInfoTO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.http.HttpStatus;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/05/27 Fri 10:40 AM
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {
    private final String UPLOAD_LOCK = "seckill:upload:lock"; // 上架秒杀用分布式锁
    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:"; // 秒杀场次key前缀
    private final String SKUKILL_CACHE_KEY = "seckill:skus";// 秒杀商品key
    private final String SKU_STOCK_SEMAPHORE_PREFIX = "seckill:stock:";// 商品库存信号量

    @Autowired
    private RedissonClient redissonClient;

    @Reference(interfaceName = "com.hatsukoi.eshopblvd.api.coupon", check = false)
    private CouponRpcService couponRpcService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Reference(interfaceName = "com.hatsukoi.eshopblvd.api.product", check = false)
    private ProductRpcService productRpcService;

    @Override
    public void uploadSeckillNext3Days() {
        log.info("定时上架秒杀场次及商品...");
        // 为避免分布式情况下多服务同时上架的情况，使用分布式锁
        RLock uploadLock = redissonClient.getLock(UPLOAD_LOCK);
        uploadLock.lock(10, TimeUnit.SECONDS);
        try {
            // 扫描最近三天需要参与秒杀的活动
            CommonResponse resp = CommonResponse.convertToResp(couponRpcService.scanNext3DaysSeckillSession());
            if (resp.getCode() == HttpStatus.SC_OK) {
                // 得到需要上架的秒杀活动以及商品
                List<SeckillSessionTo> data = resp.getData(new TypeReference<List<SeckillSessionTo>>() {
                });
                // 上架秒杀活动信息到redis
                saveSessions(data);
                // 上架秒杀商品到redis
                saveSessionSkus(data);
            }
        } finally {
            // 这里要在finally释放锁，否则如果断电了，锁永远不会被释放
            uploadLock.unlock();
        }
    }

    /**
     * 上架秒杀活动信息
     *
     * 存储的秒杀场次对应数据
     * K: SESSION_CACHE_PREFIX + startTime + "_" + endTime
     * V: sessionId+"-"+skuId的List
     * @param data
     */
    private void saveSessionSkus(List<SeckillSessionTo> data) {
        if (data != null && data.size() > 0) {
            for (SeckillSessionTo session : data) {
                long start = session.getStartTime().getTime();
                long end = session.getEndTime().getTime();
                // 活动场次的key为前缀+开始结束时间
                String key = SESSIONS_CACHE_PREFIX + start + "_" + end;
                // 保证幂等性，先查下这个活动有没有上架过，有就不要上了
                Boolean hasKey = redisTemplate.hasKey(key);
                if (!hasKey) {
                    List<String> collect = session.getSessionSkuRelations().stream().map(item -> {
                        return item.getPromotionSessionId() + "_" + item.getSkuId();
                    }).collect(Collectors.toList());
                    redisTemplate.opsForList().leftPushAll(key, collect);
                    // 设置过期时间，秒杀活动一到期缓存就可以清了
                    redisTemplate.expireAt(key, new Date(end));
                }
            }
        }
    }

    /**
     * 上架秒杀商品
     *
     * 存储的秒杀商品数据
     * K: 固定值SKUKILL_CACHE_KEY
     * V: hash，k为sessionId+"-"+skuId，v为对应的商品信息SeckillSkuRedisTo
     * @param data
     */
    private void saveSessions(List<SeckillSessionTo> data) {
        if (data != null && data.size() > 0) {
            for (SeckillSessionTo session : data) {
                // 获取秒杀sku的key
                BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_KEY);
                for (SeckillSkuRelationTo sku : session.getSessionSkuRelations()) {
                    // 每个秒杀sku的key
                    String skuKey = sku.getPromotionSessionId() + "_" + sku.getSkuId();
                    // 幂等性保证，保证这个场次下的这个商品没有上架过
                    if (!hashOps.hasKey(skuKey)) {
                        SeckillSkuRedisTo seckillSkuRedisTo = new SeckillSkuRedisTo();
                        BeanUtils.copyProperties(sku, seckillSkuRedisTo);

                        // 为每个秒杀sku生成一个随机值，秒杀开始后才能得到这个随机码，防止恶意攻击
                        String token = UUID.randomUUID().toString().replace("-", "");
                        seckillSkuRedisTo.setRandomCode(token);

                        // 保存开始结束时间
                        seckillSkuRedisTo.setStartTime(session.getStartTime().getTime());
                        seckillSkuRedisTo.setEndTime(session.getEndTime().getTime());

                        // 远程查询sku信息并保存
                        CommonResponse resp = CommonResponse.convertToResp(productRpcService.getSkuInfo(sku.getSkuId()));
                        if (resp.getCode() == HttpStatus.SC_OK) {
                            SkuInfoTO skuInfoTO = resp.getData(new TypeReference<SkuInfoTO>() {
                            });
                            seckillSkuRedisTo.setSkuInfo(skuInfoTO);
                        }

                        // 序列化为json并保存到redis
                        String jsonStr = JSON.toJSONString(seckillSkuRedisTo);
                        // 活动一过期，这些上架商品也过期，我们在获取当前商品秒杀信息的时候，做主动删除
                        hashOps.put(skuKey, jsonStr);

                        // 使用商品可以秒杀的数量作为Redisson信号量限制库存，前缀+随机码（防止恶意攻击），设置ttl
                        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE_PREFIX + token);
                        semaphore.trySetPermits(sku.getSeckillCount());
                        semaphore.expireAt(session.getEndTime());

                        // TODO: 库存服务的库存锁定数要加上上架的个数

                    }
                }
            }
        }
    }
}























