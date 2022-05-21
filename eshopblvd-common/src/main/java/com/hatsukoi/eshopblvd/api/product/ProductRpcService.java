package com.hatsukoi.eshopblvd.api.product;

import com.hatsukoi.eshopblvd.to.SkuInfoTO;

import java.util.HashMap;
import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/05/05 Thu 1:55 PM
 */
public interface ProductRpcService {
    HashMap<String, Object> getSkuInfo(Long skuId);

    HashMap<String, Object> getSkuSaleAttrsWithValue(Long skuId);

    HashMap<String, Object> getSkusPrice(List<Long> skuIds);

    HashMap<String, Object> getSpuInfoBySkuId(Long skuId);
}
