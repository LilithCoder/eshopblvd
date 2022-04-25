package com.hatsukoi.eshopblvd.product.entity;

import lombok.Data;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/04/25 Mon 1:00 PM
 */
@Data
public class SkuItemPO {
    /**
     * sku的基本信息
     */
    SkuInfo skuInfo;
    /**
     * sku的图片信息「pms_sku_images」
     */
    List<SkuImages> images;
    /**
     * sku对应spu的所有销售属性
     */
    List<SpuSaleAttrPO> saleAttrs;
    /**
     * sku对应spu的商品描述（图片）
     */
    SpuInfoDesc spuInfoDesc;
    /**
     * sku对应spu的规格参数信息
     */
    List<SpuItemAttrGroupPO> groupAttrs;
}
