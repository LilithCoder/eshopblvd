package com.hatsukoi.eshopblvd.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车项VO模型
 * @author gaoweilin
 * @date 2022/05/05 Thu 1:46 AM
 */
public class CartItemVO {
    /**
     * sku商品id
     */
    private Long skuId;
    /**
     * 购物车项的选中状态
     */
    private Boolean check = true;
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

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttr() {
        return skuAttr;
    }

    public void setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 自动计算当前购物车项的总价
     * 需要计算的属性，重写get方法，保证每次获取属性都会进行计算
     * @return
     */
    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(this.count.toString()));
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "CartItemVO{" +
                "skuId=" + skuId +
                ", check=" + check +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", skuAttr=" + skuAttr +
                ", price=" + price +
                ", count=" + count +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
