package com.hatsukoi.eshopblvd.ware.service;

import com.hatsukoi.eshopblvd.to.OrderTo;
import com.hatsukoi.eshopblvd.to.StockLockedTo;

/**
 * @author gaoweilin
 * @date 2022/04/06 Wed 2:09 AM
 */
public interface WareSkuService {
    void addStock(Long skuId, Long wareId, Integer skuNum);

    void unlockStock(StockLockedTo stockLock);

    void unlockStock(OrderTo orderTo);
}
