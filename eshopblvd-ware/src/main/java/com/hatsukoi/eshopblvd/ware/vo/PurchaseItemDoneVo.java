package com.hatsukoi.eshopblvd.ware.vo;

import lombok.Data;

/**
 * @author gaoweilin
 * @date 2022/04/06 Wed 1:30 AM
 */
@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
