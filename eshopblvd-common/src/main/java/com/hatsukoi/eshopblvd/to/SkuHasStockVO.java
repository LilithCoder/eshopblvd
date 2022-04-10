package com.hatsukoi.eshopblvd.to;

import lombok.Data;

import java.io.Serializable;

/**
 * sku是否有库存VO
 * @author gaoweilin
 * @date 2022/04/10 Sun 2:24 PM
 */
@Data
public class SkuHasStockVO implements Serializable {
    private Long skuId;
    // 是否有库存
    private Boolean hasStock;
}
