package com.hatsukoi.eshopblvd.product.service;

import com.hatsukoi.eshopblvd.product.entity.SkuImages;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/03/29 Tue 1:11 AM
 */
public interface SkuImagesService {
    void batchInsert(List<SkuImages> skuImages);

    List<SkuImages> getSkuImgsBySkuId(Long skuId);
}
