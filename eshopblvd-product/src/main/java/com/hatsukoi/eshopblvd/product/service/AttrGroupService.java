package com.hatsukoi.eshopblvd.product.service;

import com.hatsukoi.eshopblvd.product.entity.AttrGroup;
import com.hatsukoi.eshopblvd.product.vo.AttrAttrGroupRelationVO;
import com.hatsukoi.eshopblvd.product.vo.AttrGroupWithAttrsVO;
import com.hatsukoi.eshopblvd.product.vo.SkuItemVO;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;

import java.util.List;
import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/03/19 Sat 9:34 PM
 */
public interface AttrGroupService {
    CommonPageInfo<AttrGroup> queryAttrGroupPage(Map<String, Object> params, Long categoryId);

    void updateAttrGroup(AttrGroup attrGroup);

    AttrGroup getAttrGroupById(Long attrGroupId);

    void insertAttrGroup(AttrGroup attrGroup);

    void deleteAttrGroupByIds(Long[] attrGroupIds);

    void deleteRelations(AttrAttrGroupRelationVO[] relationVOs);

    List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    List<SkuItemVO.SpuItemAttrGroupVO> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}
