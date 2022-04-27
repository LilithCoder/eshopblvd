package com.hatsukoi.eshopblvd.product.entity;

import lombok.Data;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/04/25 Mon 1:03 PM
 */
@Data
public class AttrValueWithSkuIdsPO {
    /**
     * 属性值
     */
    private String attrValue;
    /**
     * 该属性值对应的哪些skuId「逗号分隔」
     */
    private String skuIds;
}
