package com.hatsukoi.eshopblvd.product.service;

import com.hatsukoi.eshopblvd.product.entity.AttrGroup;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;

import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/03/19 Sat 9:34 PM
 */
public interface AttrGroupService {
    CommonPageInfo<AttrGroup> queryAttrGroupPage(Map<String, Object> params, Long categoryId);
}
