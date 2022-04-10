package com.hatsukoi.eshopblvd.product.service;

import com.hatsukoi.eshopblvd.product.entity.ProductAttrValue;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/03/28 Mon 4:23 AM
 */
public interface ProductAttrValueService {
    void insertProductAttrValue(List<ProductAttrValue> collect);

    List<ProductAttrValue> selectBaseAttrListForSpu(Long spuId);

    void updateSpuAttr(Long spuId, List<ProductAttrValue> productAttrValueList);
}
