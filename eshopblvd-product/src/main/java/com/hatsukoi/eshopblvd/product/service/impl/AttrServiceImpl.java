package com.hatsukoi.eshopblvd.product.service.impl;

import com.github.pagehelper.PageHelper;
import com.hatsukoi.eshopblvd.constant.ProductConstant;
import com.hatsukoi.eshopblvd.product.dao.AttrAttrgroupRelationMapper;
import com.hatsukoi.eshopblvd.product.dao.AttrGroupMapper;
import com.hatsukoi.eshopblvd.product.dao.AttrMapper;
import com.hatsukoi.eshopblvd.product.dao.CategoryMapper;
import com.hatsukoi.eshopblvd.product.entity.*;
import com.hatsukoi.eshopblvd.product.service.AttrService;
import com.hatsukoi.eshopblvd.product.service.CategoryService;
import com.hatsukoi.eshopblvd.product.vo.AttrRespVO;
import com.hatsukoi.eshopblvd.product.vo.AttrVO;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
    @Autowired
    CategoryService categoryService;

    @Override
    @Transactional
    public void insertAttr(AttrVO attrVO) {
        // attr表保存基本信息
        Attr attr = new Attr();
        BeanUtils.copyProperties(attrVO, attr);
        attrMapper.insertSelective(attr);
        // attr_attrgroup_relation保存关联信息
        // 如果分组id不为空，说明是规格参数而不是销售属性，则对属性-分组表进行更新
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

    @Override
    public void batchDelete(Long[] attrIds) {
        AttrExample attrExample = new AttrExample();
        attrExample.createCriteria().andAttrIdIn(Arrays.asList(attrIds));
        attrMapper.deleteByExample(attrExample);
    }

    @Override
    public void updateAttr(AttrVO attrVO) {
        // 修改更新属性的基本信息
        Attr attr = new Attr();
        BeanUtils.copyProperties(attrVO, attr);
        attrMapper.updateByPrimaryKeySelective(attr);

        // 当属性分组不为空时，说明更新的是规则参数，则需要更新属性-分组关联表
        if (attrVO.getAttrGroupId() != null && attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelation attrAttrgroupRelation = new AttrAttrgroupRelation();
            attrAttrgroupRelation.setAttrId(attrVO.getAttrId());
            attrAttrgroupRelation.setAttrGroupId(attrVO.getAttrGroupId());
            // 查询关联表中是否属性已经有了关联的分组
            AttrAttrgroupRelationExample attrAttrgroupRelationExample = new AttrAttrgroupRelationExample();
            attrAttrgroupRelationExample.createCriteria().andAttrIdEqualTo(attrVO.getAttrId());
            List<AttrAttrgroupRelation> attrAttrgroupRelations = attrAttrgroupRelationMapper.selectByExample(attrAttrgroupRelationExample);
            // 在关联表中已有该属性分组数据时进行更新，否则插入新数据
            if (attrAttrgroupRelations.size() > 0) {
                attrAttrgroupRelationMapper.updateByExampleSelective(attrAttrgroupRelation, attrAttrgroupRelationExample);
            } else {
                attrAttrgroupRelationMapper.insertSelective(attrAttrgroupRelation);
            }
        }
    }

    @Override
    public AttrRespVO getAttrDetail(Long attrId) {
        // 查询这个属性的基本信息
        Attr attr = attrMapper.selectByPrimaryKey(attrId);
        AttrRespVO attrRespVO = new AttrRespVO();
        BeanUtils.copyProperties(attr, attrRespVO);
        // 返回的数据除了基本信息还需要分类三级路径、分类名和分组名（如果是规格参数）
        AttrAttrgroupRelationExample attrAttrgroupRelationExample = new AttrAttrgroupRelationExample();
        attrAttrgroupRelationExample.createCriteria().andAttrIdEqualTo(attrId);
        List<AttrAttrgroupRelation> attrAttrgroupRelations = attrAttrgroupRelationMapper.selectByExample(attrAttrgroupRelationExample);
        // 如果分组id不为空，通过分组表查出分组名
        if (attrAttrgroupRelations != null &&
                attrAttrgroupRelations.get(0) != null &&
                attrAttrgroupRelations.get(0).getAttrGroupId() != null) {
            Long attrGroupId = attrAttrgroupRelations.get(0).getAttrGroupId();
            AttrGroup attrGroup = attrGroupMapper.selectByPrimaryKey(attrGroupId);
            attrRespVO.setGroupName(attrGroup.getAttrGroupName());
            attrRespVO.setAttrGroupId(attrGroupId);
        }

        // 查询并设置分类名
        Category category = categoryMapper.selectByPrimaryKey(attr.getCatelogId());
        attrRespVO.setCatelogName(category.getName());

        // 查询并设置分类名
        Long[] catelogPath = categoryService.getCatelogPath(attr.getCatelogId());
        attrRespVO.setCatelogPath(catelogPath);
        return attrRespVO;
    }

    /**
     * 根据分组id来查找与之相关的规格参数
     * @param attrgroupId
     * @return
     */
    @Override
    public List<Attr> getRelatedAttrsByAttrGroup(Long attrgroupId) {
        // 首先根据关联表查找所有跟这个分组有关的基本属性
        AttrAttrgroupRelationExample attrAttrgroupRelationExample = new AttrAttrgroupRelationExample();
        attrAttrgroupRelationExample.createCriteria().andAttrGroupIdEqualTo(attrgroupId);
        List<AttrAttrgroupRelation> attrAttrgroupRelations = attrAttrgroupRelationMapper.selectByExample(attrAttrgroupRelationExample);

        // 如果没有关联的属性，就返回null
        if (attrAttrgroupRelations == null || attrAttrgroupRelations.size() == 0) {
            return null;
        }

        // 如果有的话，则根据这些相关的属性的id去查询所有的基本属性列表，并返回
        List<Long> attrIds = attrAttrgroupRelations.stream().map((relation) -> {
            return relation.getAttrId();
        }).collect(Collectors.toList());
        AttrExample attrExample = new AttrExample();
        attrExample.createCriteria().andAttrIdIn(attrIds);
        List<Attr> attrs = attrMapper.selectByExample(attrExample);

        return attrs;
    }

    /**
     * 获取当前分组在所属分类下还没有关联的所有基本属性
     * @param attrgroupId
     * @param params
     * @return
     */
    @Override
    public CommonPageInfo<Attr> getNonRelatedAttrsByAttrGroup(Long attrgroupId, Map<String, Object> params) {
        // 分页参数
        int pageNum = 1;
        int pageSize = 10;
        // 模糊搜索关键词
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

        // 获取分组所属当前分类id（分组表）
        AttrGroup attrGroup = attrGroupMapper.selectByPrimaryKey(attrgroupId);
        Long catelogId = attrGroup.getCatelogId();

        // 获取当前分类下所有的分组的id（分组表）
        AttrGroupExample example = new AttrGroupExample();
        example.createCriteria().andCatelogIdEqualTo(catelogId);
        List<AttrGroup> attrGroups = attrGroupMapper.selectByExample(example);
        List<Long> attrGroupIds = attrGroups.stream().map((item) -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());

        // 获取这些分组的已经关联的所有基本属性（属性-分组表）
        AttrAttrgroupRelationExample attrAttrgroupRelationExample = new AttrAttrgroupRelationExample();
        attrAttrgroupRelationExample.createCriteria().andAttrGroupIdIn(attrGroupIds);
        List<AttrAttrgroupRelation> relations = attrAttrgroupRelationMapper.selectByExample(attrAttrgroupRelationExample);
        List<Long> attrIds = relations.stream().map((relation) -> {
            return relation.getAttrId();
        }).collect(Collectors.toList());

        // 从当前分类的下所有基本属性中移除上述查到属性，且根据关键词来匹配属性id和模糊匹配属性名（属性表）
        // select * from pms_attr
        // where
        // (catelog_id=catelogId) and
        // (attr_id not in attrIds) and
        // (attr_type=1) and
        // (attr_id=#{key} or attr_name like %#{key}%)
        AttrExample attrExample = new AttrExample();
        AttrExample.Criteria criteria = attrExample.createCriteria();
        criteria.andCatelogIdEqualTo(catelogId)
                .andAttrTypeEqualTo((byte) ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (attrIds != null && attrIds.size() > 0) {
            criteria.andAttrIdNotIn(attrIds);
        }
        if (!StringUtils.isEmpty(key)) {
            criteria.andKeyQuery(key);
        }
        List<Attr> attrs = attrMapper.selectByExample(attrExample);
        // 封装成分页数据并返回
        return CommonPageInfo.convertToCommonPage(attrs);
    }
}
