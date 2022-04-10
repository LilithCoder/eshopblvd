package com.hatsukoi.eshopblvd.product.service;

import com.hatsukoi.eshopblvd.product.entity.SkuInfo;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;

import java.util.List;
import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/03/29 Tue 1:02 AM
 */
public interface SkuInfoService {
    void insertSkuInfo(SkuInfo skuInfo);

    CommonPageInfo<SkuInfo> querySkuPageByFilters(Map<String, Object> params);

    List<SkuInfo> getSkusBySpuId(Long spuId);
}
