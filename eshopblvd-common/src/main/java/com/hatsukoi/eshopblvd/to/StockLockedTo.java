package com.hatsukoi.eshopblvd.to;

import lombok.Data;

/**
 * @author gaoweilin
 * @date 2022/05/20 Fri 12:29 AM
 */
@Data
public class StockLockedTo {
    /**
     * id
     */
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;
    /**
     * 仓库id
     */
    private Long wareId;
    /**
     * 1-已锁定  2-已解锁  3-扣减
     */
    private Integer lockStatus;
}
