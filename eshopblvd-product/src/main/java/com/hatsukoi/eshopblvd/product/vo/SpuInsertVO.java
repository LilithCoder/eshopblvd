package com.hatsukoi.eshopblvd.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 新增商品VO
 * @author gaoweilin
 * @date 2022/03/28 Mon 2:09 AM
 */
@Data
public class SpuInsertVO {
    /**
     * 商品名称
     */
    private String spuName;
    /**
     * 商品描述
     */
    private String spuDescription;
    /**
     * 所属分类id
     */
    private Long catalogId;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 商品重量
     */
    private BigDecimal weight;
    /**
     * 上架状态[0 - 下架，1 - 上架]
     */
    private Byte publishStatus;
    /**
     * 商品描述图片地址
     */
    private List<String> decript;
    /**
     * 商品图集（sku用）
     */
    private List<String> images;
    /**
     * 积分信息
     */
    private Bounds bounds;
    /**
     * 规格参数
     */
    private List<BaseAttr> baseAttrs;
    /**
     * 所属sku的信息
     */
    private List<Sku> skus;
}
