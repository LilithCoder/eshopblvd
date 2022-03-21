package com.hatsukoi.eshopblvd.product.service.impl;

import com.github.pagehelper.PageHelper;
import com.hatsukoi.eshopblvd.constant.ProductConstant;
import com.hatsukoi.eshopblvd.product.dao.AttrAttrgroupRelationMapper;
import com.hatsukoi.eshopblvd.product.dao.AttrGroupMapper;
import com.hatsukoi.eshopblvd.product.dao.AttrMapper;
import com.hatsukoi.eshopblvd.product.dao.CategoryMapper;
import com.hatsukoi.eshopblvd.product.entity.*;
import com.hatsukoi.eshopblvd.product.service.AttrService;
import com.hatsukoi.eshopblvd.product.vo.AttrRespVO;
import com.hatsukoi.eshopblvd.product.vo.AttrVO;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    AttrGroupMapper attrGroupMapper;

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

    @Override
    public CommonPageInfo<AttrRespVO> queryAttrPage(Map<String, Object> params, String attrType, Long catelogId) {
        // 分页参数
        int pageNum = 1;
        int pageSize = 10;
        // 搜索关键词
        String key = "";
        if (params.get("page") != null) {
            pageNum = Integer.parseInt(params.get("page").toString());
        }
        if (params.get("limit") != null) {
            pageSize = Integer.parseInt(params.get("limit").toString());
        }
        if (params.get("key") != null) {
            key = params.get("key").toString();
        }
        PageHelper.startPage(pageNum, pageSize);
        // select * from pms_attr where (attr_type=attrType and catelog_id=catelogId and (attr_name like %key% or attr_id=key))
        AttrExample attrExample = new AttrExample();
        AttrExample.Criteria criteria = attrExample.createCriteria();
        // 根据attrType进行查询：0销售属性, 1规格参数
        if (!StringUtils.isEmpty(attrType)) {
            byte attrTypeCode = (byte) (ProductConstant.AttrEnum.ATTR_TYPE_BASE.getMsg().equalsIgnoreCase(attrType)
                                ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                                : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
            criteria.andAttrTypeEqualTo(attrTypeCode);
        }
        // 根据所选分类来查询，如果分类id为0，那么就查询全部分类下的属性
        if (catelogId != 0) {
            criteria.andCatelogIdEqualTo(catelogId);
        }
        // 搜索查询：匹配属性id或者模糊匹配属性名
        if (!StringUtils.isEmpty(key)) {
            criteria.andKeyQuery(key);
        }
        // 根据种种条件查询获取属性列表
        List<Attr> attrs = attrMapper.selectByExample(attrExample);
        List<AttrRespVO> attrRespVOs = attrs.stream().map(attr -> {
            AttrRespVO attrRespVO = new AttrRespVO();
            BeanUtils.copyProperties(attr, attrRespVO);
            // 从分类表中查询分类名并设置
            Category category = categoryMapper.selectByPrimaryKey(attr.getCatelogId());
            attrRespVO.setCatelogName(category.getName());
            // 只有是规格参数才查询并设置属性分组名，因为商品属性没有属性分组
            if (ProductConstant.AttrEnum.ATTR_TYPE_BASE.getMsg().equalsIgnoreCase(attrType)) {
                AttrAttrgroupRelationExample attrAttrgroupRelationExample = new AttrAttrgroupRelationExample();
                attrAttrgroupRelationExample.createCriteria().andAttrIdEqualTo(attr.getAttrId());
                // 从属性-属性分组关联表中查出属性id对应的属性分组id
                List<AttrAttrgroupRelation> attrAttrgroupRelations = attrAttrgroupRelationMapper.selectByExample(attrAttrgroupRelationExample);
                // 如果这个规格参数有对应的分组的话
                if (attrAttrgroupRelations != null &&
                        attrAttrgroupRelations.size() == 1 &&
                        attrAttrgroupRelations.get(0).getAttrGroupId() != null) {
                    // 再从属性分组关联表中根据属性分组id查出属性分组名
                    Long attrGroupId = attrAttrgroupRelations.get(0).getAttrGroupId();
                    AttrGroup attrGroup = attrGroupMapper.selectByPrimaryKey(attrGroupId);
                    // 设置分组名
                    attrRespVO.setGroupName(attrGroup.getAttrGroupName());
                }
            }
            return attrRespVO;
        }).collect(Collectors.toList());
        return CommonPageInfo.convertToCommonPage(attrRespVOs);
    }
}
