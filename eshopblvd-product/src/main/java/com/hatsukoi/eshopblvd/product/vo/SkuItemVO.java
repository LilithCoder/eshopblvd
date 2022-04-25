package com.hatsukoi.eshopblvd.product.vo;

import com.hatsukoi.eshopblvd.product.entity.SkuImages;
import com.hatsukoi.eshopblvd.product.entity.SkuInfo;
import com.hatsukoi.eshopblvd.product.entity.SpuInfoDesc;
import lombok.Data;

import java.util.List;

/**
 * 详情页sku的返回结果数据模型
 * @author gaoweilin
 * @date 2022/04/25 Mon 12:56 AM
 */
@Data
public class SkuItemVO {
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
    List<SpuSaleAttrVO> saleAttrs;
    /**
     * sku对应spu的商品描述（图片）
     */
    SpuInfoDesc spuInfoDesc;
    /**
     * sku对应spu的规格参数信息
     */
    List<SpuItemAttrGroupVO> groupAttrs;

    @Data
    public static class SpuSaleAttrVO {
        /**
         * 属性id
         */
        private Long attrId;
        /**
         * 属性名
         */
        private String attrName;
        /**
         * 对应的属性值们，且每个属性值还有对应的sku列表
         */
        private List<AttrValueWithSkuIdsVO> attrValues;
    }

    /**
     * sku的销售属性值with对应的sku们
     */
    @Data
    public static class AttrValueWithSkuIdsVO {
        /**
         * 属性值
         */
        private String attrValue;
        /**
         * 该属性值对应的哪些skuId
         */
        private List<Long> skuIds;
    }

    /**
     * sku对应spu的属性分组，还包含了每个分组下规格参数
     */
    @Data
    public static class SpuItemAttrGroupVO {
        /**
         * 属性分组名
         */
        private String groupName;
        /**
         * 属性分组对应的规格参数
         */
        private List<BaseAttrVO> attrs;
    }

    /**
     * sku要展示的规格参数
     */
    @Data
    public static class BaseAttrVO {
        /**
         * 属性id
         */
        private Long attrId;
        /**
         * 属性名
         */
        private String attrName;
        /**
         * 属性值
         */
        private String attrValue;
    }
}
