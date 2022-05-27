package com.hatsukoi.eshopblvd.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author gaoweilin
 * @date 2022/05/27 Fri 3:03 PM
 */
@Data
public class SeckillOrderTo {
    private String orderSn; //订单号
    private Long promotionSessionId;  //活动场次id
    private Long skuId;  //商品id
    private BigDecimal seckillPrice; //秒杀价格
    private Integer num; //购买数量
    private Long memberId;//会员id
}
