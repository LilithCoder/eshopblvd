package com.hatsukoi.eshopblvd.to;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author gaoweilin
 * @date 2022/05/09 Mon 10:55 PM
 */
@Data
public class SkuPriceTO implements Serializable {
    /**
     * sku标识
     */
    private Long skuId;
    /**
     * sku价格
     */
    private BigDecimal price;
}
