package com.hatsukoi.eshopblvd.product.entity;

import com.hatsukoi.eshopblvd.product.vo.SkuItemVO;
import lombok.Data;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/04/25 Mon 1:02 PM
 */
@Data
public class SpuSaleAttrPO {
    /**
     * 属性id
     */
    private Long attrId;
    /**
     * 属性名
     */
    private String attrName;
    /**
     * 对应的属性值们，且每个属性值还有对应的sku列表
     */
    private List<AttrValueWithSkuIdsPO> attrValues;
}
