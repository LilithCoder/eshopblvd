package com.hatsukoi.eshopblvd.ware.service;

import com.hatsukoi.eshopblvd.to.WareSkuLockTo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;

import java.util.HashMap;
import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/04/10 Sun 2:16 PM
 */
public interface WareSkuRPCService {
    /**
     * 获取这些sku是否还有库存
     * @param skuIds
     * @return
     */
    HashMap<String, Object> getSkusHasStock(List<Long> skuIds);

    /**
     * 根据地址id返回地址信息和相应的运费
     * @param addrId
     * @return
     */
    HashMap<String, Object> getAddrInfoAndFare(Long addrId);

    /**
     * 给这些订单项锁库存
     * @param wareSkuLock
     * @return
     */
    HashMap<String, Object> wareSkuLock(WareSkuLockTo wareSkuLock);
}
