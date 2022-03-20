package com.hatsukoi.eshopblvd.product.service.impl;

import com.hatsukoi.eshopblvd.product.dao.AttrAttrgroupRelationMapper;
import com.hatsukoi.eshopblvd.product.dao.AttrMapper;
import com.hatsukoi.eshopblvd.product.entity.Attr;
import com.hatsukoi.eshopblvd.product.entity.AttrAttrgroupRelation;
import com.hatsukoi.eshopblvd.product.service.AttrService;
import com.hatsukoi.eshopblvd.product.vo.AttrVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author gaoweilin
 * @date 2022/03/20 Sun 4:10 PM
 */
@Service
public class AttrServiceImpl implements AttrService {
    @Autowired
    AttrMapper attrMapper;

    @Autowired
    AttrAttrgroupRelationMapper attrAttrgroupRelationMapper;

    @Override
    @Transactional
    public void insertAttr(AttrVO attrVO) {
        // attr表保存基本信息
        Attr attr = new Attr();
        BeanUtils.copyProperties(attrVO, attr);
        attrMapper.insertSelective(attr);
        // attr_attrgroup_relation保存关联信息
        if (attrVO.getAttrGroupId() != null) {
            AttrAttrgroupRelation attrAttrgroupRelation = new AttrAttrgroupRelation();
            attrAttrgroupRelation.setAttrId(attr.getAttrId());
            attrAttrgroupRelation.setAttrGroupId(attrVO.getAttrGroupId());
            attrAttrgroupRelationMapper.insert(attrAttrgroupRelation);
        }
    }
}
