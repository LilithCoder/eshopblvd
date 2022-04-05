package com.hatsukoi.eshopblvd.ware.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/04/05 Tue 2:13 AM
 */
@Data
public class MergeVO implements Serializable {
    /**
     * 目标合并的采购单id
     */
    private Long purchaseId;
    /**
     * 需要合并的采购需求
     */
    private List<Long> items;
}
