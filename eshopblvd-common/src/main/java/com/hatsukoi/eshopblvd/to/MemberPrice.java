package com.hatsukoi.eshopblvd.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author gaoweilin
 * @date 2022/03/29 Tue 1:59 AM
 */
@Data
public class MemberPrice {
    private Long id;
    private String name;
    private BigDecimal price;
}
