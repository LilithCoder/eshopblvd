package com.hatsukoi.eshopblvd.product.vo;

import lombok.Data;

/**
 * 规格参数
 * @author gaoweilin
 * @date 2022/03/28 Mon 2:29 AM
 */
@Data
public class BaseAttr {
    /**
     * 属性id
     */
    private Long attrId;
    /**
     * 属性值
     * 可有多个，用分号隔开
     */
    private String attrValues;
    /**
     * 快速展示【是否展示在介绍上；0-否 1-是】
     */
    private int showDesc;
}
