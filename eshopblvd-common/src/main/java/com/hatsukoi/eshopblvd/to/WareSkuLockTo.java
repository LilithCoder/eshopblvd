package com.hatsukoi.eshopblvd.to;

import com.hatsukoi.eshopblvd.vo.OrderItemVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/05/19 Thu 1:36 PM
 */
@Data
public class WareSkuLockTo implements Serializable {
    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 需要锁住的所有库存信息
     */
    private List<OrderItemVO> locks;
}
