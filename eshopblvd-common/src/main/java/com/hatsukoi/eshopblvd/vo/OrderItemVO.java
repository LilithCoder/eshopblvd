package com.hatsukoi.eshopblvd.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/05/09 Mon 1:29 AM
 */
@Data
public class OrderItemVO implements Serializable {
    /**
     * sku商品id
     */
    private Long skuId;
    /**
     * sku标题
     */
    private String title;
    /**
     * sku图片
     */
    private String image;
    /**
     * sku的商品属性列表
     * [
     *    "属性1: 属性值1",
     *    "属性2: 属性值2"
     * ]
     */
    private List<String> skuAttr;
    /**
     * sku商品单价
     */
    private BigDecimal price;
    /**
     * 商品数量
     */
    private Integer count;
    /**
     * 当前购物车项算上数量后的总价
     */
    private BigDecimal totalPrice;
    /**
     * 物品重量
     */
    private BigDecimal weight;
    /**
     * 是否有库存
     */
    private Boolean hasStock;
}
