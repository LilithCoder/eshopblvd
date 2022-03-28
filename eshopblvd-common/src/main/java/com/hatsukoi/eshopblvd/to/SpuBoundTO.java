package com.hatsukoi.eshopblvd.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author gaoweilin
 * @date 2022/03/28 Mon 10:41 PM
 */
@Data
public class SpuBoundTO {
    private Long spuId;
    private BigDecimal growBounds;
    private BigDecimal buyBounds;
}
