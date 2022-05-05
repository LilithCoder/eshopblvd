package com.hatsukoi.eshopblvd.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车VO模型
 * @author gaoweilin
 * @date 2022/05/05 Thu 1:46 AM
 */
public class CartVO {
    /**
     * 购物车项列表
     */
    List<CartItemVO> items;
    /**
     * 商品总计数量（每个商品数量遍历后的累积）
     */
    private Integer countNum;
    /**
     * 商品项的数量（商品类型数量）
     */
    private Integer countType;
    /**
     * 购物车中所有被选中商品的累计最终价格（已减去优惠价格）
     */
    private BigDecimal totalAmount;
    /**
     * 减免优惠价格
     */
    private BigDecimal reduce = new BigDecimal("0.00");

    public List<CartItemVO> getItems() {
        return items;
    }

    public void setItems(List<CartItemVO> items) {
        this.items = items;
    }

    /**
     * 自动计算当前购物车商品总计数量
     * 需要计算的属性，重写get方法，保证每次获取属性都会进行计算
     * @return
     */
    public Integer getCountNum() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItemVO item: items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public void setCountNum(Integer countNum) {
        this.countNum = countNum;
    }

    /**
     * 自动计算当前购物车商品项的数量
     * 需要计算的属性，重写get方法，保证每次获取属性都会进行计算
     * @return
     */
    public Integer getCountType() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItemVO item: items) {
                count++;
            }
        }
        return count;
    }

    public void setCountType(Integer countType) {
        this.countType = countType;
    }

    /**
     * 自动计算当前购物车中所有被选中商品的累计最终价格
     * 需要计算的属性，重写get方法，保证每次获取属性都会进行计算
     * @return
     */
    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0.00");
        // 1. 计算选中购物项的总价
        if (items != null && items.size() > 0) {
            for (CartItemVO item: items) {
                if (item.getCheck()) {
                    amount = amount.add(item.getTotalPrice());
                }
            }
        }
        // 2. 减去优惠价格
        amount = amount.subtract(this.reduce);
        return amount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }

    @Override
    public String toString() {
        return "CartVO{" +
                "items=" + items +
                ", countNum=" + countNum +
                ", countType=" + countType +
                ", totalAmount=" + totalAmount +
                ", reduce=" + reduce +
                '}';
    }
}
