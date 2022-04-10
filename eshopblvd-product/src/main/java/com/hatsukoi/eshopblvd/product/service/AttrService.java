package com.hatsukoi.eshopblvd.product.service;

import com.hatsukoi.eshopblvd.product.entity.Attr;
import com.hatsukoi.eshopblvd.product.vo.AttrRespVO;
import com.hatsukoi.eshopblvd.product.vo.AttrVO;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;

import java.util.List;
import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/03/20 Sun 4:10 PM
 */
public interface AttrService {
    void insertAttr(AttrVO attrVO);

    CommonPageInfo<AttrRespVO> queryAttrPage(Map<String, Object> params, String attrType, Long catelogId);

    void batchDelete(Long[] attrIds);

    void updateAttr(AttrVO attrVO);

    AttrRespVO getAttrDetail(Long attrId);

    List<Attr> getRelatedAttrsByAttrGroup(Long attrgroupId);

    CommonPageInfo<Attr> getNonRelatedAttrsByAttrGroup(Long attrgroupId, Map<String, Object> params);

    Attr getAttrById(Long attrId);

    List<Long> filterAttrIds(List<Long> attrIds);
}
