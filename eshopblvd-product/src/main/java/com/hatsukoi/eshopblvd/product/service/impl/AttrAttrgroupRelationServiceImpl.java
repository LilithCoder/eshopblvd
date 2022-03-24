package com.hatsukoi.eshopblvd.product.service.impl;

import com.hatsukoi.eshopblvd.product.dao.AttrAttrgroupRelationMapper;
import com.hatsukoi.eshopblvd.product.entity.AttrAttrgroupRelation;
import com.hatsukoi.eshopblvd.product.service.AttrAttrgroupRelationService;
import com.hatsukoi.eshopblvd.product.vo.AttrAttrGroupRelationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/03/24 Thu 12:31 AM
 */
@Service
public class AttrAttrgroupRelationServiceImpl implements AttrAttrgroupRelationService {
    @Autowired
    AttrAttrgroupRelationMapper attrAttrgroupRelationMapper;

    @Override
    public void batchInsertRelations(List<AttrAttrGroupRelationVO> relationVOs) {
        List<AttrAttrgroupRelation> relations = relationVOs.stream().map((vo) -> {
            AttrAttrgroupRelation attrAttrgroupRelation = new AttrAttrgroupRelation();
            BeanUtils.copyProperties(vo, attrAttrgroupRelation);
            return attrAttrgroupRelation;
        }).collect(Collectors.toList());
        attrAttrgroupRelationMapper.batchInsert(relations);
    }
}
