package com.hatsukoi.eshopblvd.product.vo;

import com.hatsukoi.eshopblvd.product.entity.Attr;
import lombok.Data;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/03/25 Fri 3:33 AM
 */
@Data
public class AttrGroupWithAttrsVO {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<Attr> attrs;
}
