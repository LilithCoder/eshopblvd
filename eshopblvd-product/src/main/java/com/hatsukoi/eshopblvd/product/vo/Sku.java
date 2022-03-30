package com.hatsukoi.eshopblvd.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Sku的VO
 * @author gaoweilin
 * @date 2022/03/28 Mon 2:34 AM
 */
@Data
public class Sku {
    /**
     * sku的销售属性
     */
    private List<SaleAttr> attr;
    /**
     * sku名称
     */
    private String skuName;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 标题
     */
    private String skuTitle;
    /**
     * 副标题
     */
    private String skuSubtitle;
    /**
     * sku商品图
     */
    private List<Image> images;
    /**
     * 销售属性组合（笛卡尔积）
     * e.g: ["黑色", "6GB"]
     */
    private List<String> descar;
    /**
     * 满几件
     */
    private int fullCount;
    /**
     * 打几折
     */
    private BigDecimal discount;
    /**
     * 满折是否叠加其他优惠[0-不可叠加，1-可叠加]
     */
    private int countStatus;
    /**
     * 满多少
     */
    private BigDecimal fullPrice;
    /**
     * 减多少
     */
    private BigDecimal reducePrice;
    /**
     * 满减是否参与其他优惠
     */
    private int priceStatus;
    /**
     * 会员价格
     */
    private List<MemberPrice> memberPrice;
}
