package com.hatsukoi.eshopblvd.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @author gaoweilin
 * @date 2022/03/28 Mon 2:20 AM
 */
@Data
public class Bounds {
    /**
     * 成长积分
     */
    private BigDecimal growBounds;
    /**
     * 购物积分（金币）
     */
    private BigDecimal buyBounds;
}
