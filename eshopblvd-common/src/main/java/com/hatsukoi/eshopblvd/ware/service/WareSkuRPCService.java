package com.hatsukoi.eshopblvd.ware.service;

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
}
