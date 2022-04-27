package com.hatsukoi.eshopblvd.product.entity;

import lombok.Data;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/04/25 Mon 1:04 PM
 */
@Data
public class BaseAttrPO {
    /**
     * 属性id
     */
    private Long attrId;
    /**
     * 属性名
     */
    private String attrName;
    /**
     * 属性值「可有多个值，逗号分隔」
     */
    private String attrValue;
}
