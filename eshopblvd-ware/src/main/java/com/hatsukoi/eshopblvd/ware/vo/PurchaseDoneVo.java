package com.hatsukoi.eshopblvd.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/04/06 Wed 1:29 AM
 */
@Data
public class PurchaseDoneVo implements Serializable {
    /**
     * 采购单id
     */
    @NotNull
    private Long id;
    private List<PurchaseItemDoneVo> items;
}
