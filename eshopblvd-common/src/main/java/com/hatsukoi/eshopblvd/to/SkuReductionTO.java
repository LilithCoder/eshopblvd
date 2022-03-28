package com.hatsukoi.eshopblvd.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/03/29 Tue 1:58 AM
 */
@Data
public class SkuReductionTO {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
