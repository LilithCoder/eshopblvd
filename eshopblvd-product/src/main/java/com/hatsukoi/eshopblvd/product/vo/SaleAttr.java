package com.hatsukoi.eshopblvd.product.vo;

import lombok.Data;

/**
 * sku的商品属性
 * @author gaoweilin
 * @date 2022/03/28 Mon 2:41 AM
 */
@Data
public class SaleAttr {
    private Long attrId;
    private String attrName;
    private String attrValue;
}
