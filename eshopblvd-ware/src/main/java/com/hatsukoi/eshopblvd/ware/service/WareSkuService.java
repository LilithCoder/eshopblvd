package com.hatsukoi.eshopblvd.ware.service;

/**
 * @author gaoweilin
 * @date 2022/04/06 Wed 2:09 AM
 */
public interface WareSkuService {
    void addStock(Long skuId, Long wareId, Integer skuNum);
}
