package com.hatsukoi.eshopblvd.ware.service.impl;

import com.github.pagehelper.PageHelper;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.ware.dao.WareInfoMapper;
import com.hatsukoi.eshopblvd.ware.entity.WareInfo;
import com.hatsukoi.eshopblvd.ware.entity.WareInfoExample;
import com.hatsukoi.eshopblvd.ware.service.WareInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/04/03 Sun 3:46 PM
 */
@Service
public class WareInfoServiceImpl implements WareInfoService {
    @Autowired
    private WareInfoMapper wareInfoMapper;

    @Override
    public CommonPageInfo<WareInfo> queryWareInfoPage(Map<String, Object> params) {
        // 分页参数
        int pageNum = 1;
        int pageSize = 10;
        String pageNumStr = params.get("page").toString();
        if (!StringUtils.isEmpty(pageNumStr)) {
            pageNum = Integer.parseInt(pageNumStr);
        }
        String pageSizeStr = params.get("limit").toString();
        if (!StringUtils.isEmpty(pageSizeStr)) {
            pageSize = Integer.parseInt(pageSizeStr);
        }
        PageHelper.startPage(pageNum, pageSize);

        // select * from wms_ware_info
        // where
        // (name like %#{keyword}%) or
        // (address like %#{keyword}% or
        // (areacode like %#{keyword}%) or
        // (id=#{keyword})
        WareInfoExample wareInfoExample = new WareInfoExample();
        // 模糊搜索关键词
        String keyword = params.get("key").toString();

        // 第一个criteria建立的时候会加入oredCriteria
        WareInfoExample.Criteria criteria1 = wareInfoExample.createCriteria();

        if (!StringUtils.isEmpty(keyword)) {
            // 后续criteria建立的时候不会加入oredCriteria，所以要or(criteria)手动加入
            criteria1.andNameLike("%" + keyword + "%");

            WareInfoExample.Criteria criteria2 = wareInfoExample.createCriteria();
            criteria2.andAddressLike("%" + keyword + "%");
            wareInfoExample.or(criteria2);

            WareInfoExample.Criteria criteria3 = wareInfoExample.createCriteria();
            criteria3.andAreacodeLike("%" + keyword + "%");
            wareInfoExample.or(criteria3);

            if (StringUtils.isNumeric(keyword)) {
                WareInfoExample.Criteria criteria4 = wareInfoExample.createCriteria();
                criteria4.andIdEqualTo(Long.parseLong(keyword));
                wareInfoExample.or(criteria4);
            }
        }
        List<WareInfo> wareInfos = wareInfoMapper.selectByExample(wareInfoExample);
        return CommonPageInfo.convertToCommonPage(wareInfos);
    }
}
