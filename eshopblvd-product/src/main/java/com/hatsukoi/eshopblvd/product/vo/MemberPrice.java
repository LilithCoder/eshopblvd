package com.hatsukoi.eshopblvd.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author gaoweilin
 * @date 2022/03/28 Mon 2:55 AM
 */
@Data
public class MemberPrice {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 会员等级名
     * e.g: 铜牌会员
     */
    private String name;
    /**
     * 会员对应价格
     */
    private BigDecimal price;
}
