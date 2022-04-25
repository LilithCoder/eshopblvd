package com.hatsukoi.eshopblvd.product.entity;

import com.hatsukoi.eshopblvd.product.vo.SkuItemVO;
import lombok.Data;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/04/25 Mon 1:03 PM
 */
@Data
public class SpuItemAttrGroupPO {
    /**
     * 属性分组名
     */
    private String groupName;
    /**
     * 属性分组对应的规格参数
     */
    private List<BaseAttrPO> attrs;
}
