package com.hatsukoi.eshopblvd.to;

import com.hatsukoi.eshopblvd.to.SkuInfoTO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author gaoweilin
 * @date 2022/05/27 Fri 11:46 AM
 */
@Data
public class SeckillSkuRedisTo {
    /**
     * 活动id
     */
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private Integer seckillCount;
    /**
     * 每人限购数量
     */
    private Integer seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;
    /**
     * 商品秒杀随机码
     */
    private String randomCode;
    /**
     * 当前商品秒杀的开始时间
     */
    private Long startTime;
    /**
     * 当前商品秒杀的结束时间
     */
    private Long endTime;
    /**
     * sku的详细信息
     */
    private SkuInfoTO skuInfo;
}
