package com.hatsukoi.eshopblvd.product.service;

import com.hatsukoi.eshopblvd.product.vo.AttrAttrGroupRelationVO;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/03/24 Thu 12:30 AM
 */
public interface AttrAttrgroupRelationService {
    void batchInsertRelations(List<AttrAttrGroupRelationVO> relationVOs);
}
