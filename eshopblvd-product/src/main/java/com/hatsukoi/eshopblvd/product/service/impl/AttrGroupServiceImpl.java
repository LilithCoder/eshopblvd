package com.hatsukoi.eshopblvd.product.service.impl;

import com.github.pagehelper.PageHelper;
import com.hatsukoi.eshopblvd.product.dao.AttrGroupMapper;
import com.hatsukoi.eshopblvd.product.entity.AttrGroup;
import com.hatsukoi.eshopblvd.product.entity.AttrGroupExample;
import com.hatsukoi.eshopblvd.product.service.AttrGroupService;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/03/19 Sat 9:34 PM
 */
@Service
public class AttrGroupServiceImpl implements AttrGroupService {

    @Autowired
    AttrGroupMapper attrGroupMapper;

    @Override
    public CommonPageInfo<AttrGroup> queryAttrGroupPage(Map<String, Object> params, Long categoryId) {
        // 分页参数
        int pageNum = 1;
        int pageSize = 10;
        // 模糊搜索关键词
        String keyword = "";
        if (params.get("page") != null) {
            pageNum = Integer.parseInt(params.get("page").toString());
        }
        if (params.get("limit") != null) {
            pageSize = Integer.parseInt(params.get("limit").toString());
        }
        if (params.get("key") != null) {
            keyword = params.get("key").toString();
        }
        PageHelper.startPage(pageNum, pageSize);
        // select * from pms_attr_group where catelog_id=category and (attr_group_id=keyword or att_group_name like %keyword%)
        // %作为通配符替代 0 个或多个字符
        AttrGroupExample example = new AttrGroupExample();
        AttrGroupExample.Criteria criteria = example.createCriteria();
        if (categoryId != 0) {
            criteria.andCatelogIdEqualTo(categoryId);
        }
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andKeywordFilter(keyword);
        }
        List<AttrGroup> attrGroups = attrGroupMapper.selectByExample(example);
        return CommonPageInfo.convertToCommonPage(attrGroups);
    }

    @Override
    public void updateAttrGroup(AttrGroup attrGroup) {
        attrGroupMapper.updateByPrimaryKeySelective(attrGroup);
    }

    @Override
    public AttrGroup getAttrGroupById(Long attrGroupId) {
        return attrGroupMapper.selectByPrimaryKey(attrGroupId);
    }

    @Override
    public void insertAttrGroup(AttrGroup attrGroup) {
        attrGroupMapper.insert(attrGroup);
    }

    @Override
    public void deleteAttrGroupByIds(Long[] attrGroupIds) {
        AttrGroupExample example = new AttrGroupExample();
        example.createCriteria().andAttrGroupIdIn(Arrays.asList(attrGroupIds));
        attrGroupMapper.deleteByExample(example);
    }
}