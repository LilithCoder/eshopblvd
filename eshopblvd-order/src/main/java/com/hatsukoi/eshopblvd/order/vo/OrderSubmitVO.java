package com.hatsukoi.eshopblvd.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单提交的数据
 * （购买的商品需要从购物车里再获取一遍最新的，以防用户有修改）
 * @author gaoweilin
 * @date 2022/05/18 Wed 12:04 PM
 */
@Data
public class OrderSubmitVO {
    /**
     * 收货地址id
     */
    private Long addrId;
    /**
     * 支付方式
     */
    private Integer payType;
    /**
     * 应付价格
     */
    private BigDecimal payPrice;
    /**
     * 订单备注
     */
    private String note;
    /**
     * 防重令牌
     */
    private String orderToken;
}
