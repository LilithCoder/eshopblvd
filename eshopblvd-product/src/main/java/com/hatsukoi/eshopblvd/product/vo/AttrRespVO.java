package com.hatsukoi.eshopblvd.product.vo;

import lombok.Data;

/**
 * @author gaoweilin
 * @date 2022/03/22 Tue 2:15 AM
 */
@Data
public class AttrRespVO extends AttrVO{
    /**
     * 所属分类名字
     * e.g: "手机"
     */
    private String catelogName;
    /**
     * 所属分组名字
     * e.g: "主体"
     * 只有规格参数有这个字段
     */
    private String groupName;
    /**
     * 所属分类id的路径
     * e.g: [2, 34, 225]
     */
    private Long[] catelogPath;
}
