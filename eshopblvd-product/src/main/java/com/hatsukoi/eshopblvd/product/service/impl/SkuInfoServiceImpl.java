package com.hatsukoi.eshopblvd.product.service.impl;

import com.github.pagehelper.PageHelper;
import com.hatsukoi.eshopblvd.product.dao.SkuInfoMapper;
import com.hatsukoi.eshopblvd.product.entity.SkuInfo;
import com.hatsukoi.eshopblvd.product.entity.SkuInfoExample;
import com.hatsukoi.eshopblvd.product.service.SkuInfoService;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/03/29 Tue 1:02 AM
 */
@Service
public class SkuInfoServiceImpl implements SkuInfoService {
    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Override
    public void insertSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insert(skuInfo);
    }

    /**
     * {
     * page: 1,//当前页码
     * limit: 10,//每页记录数
     * sidx: 'id',//排序字段
     * order: 'asc/desc',//排序方式
     * key: '华为',//检索关键字
     * catelogId: 0,
     * brandId: 0,
     * min: 0,
     * max: 0
     * }
     * @param params
     * @return
     */
    @Override
    public CommonPageInfo<SkuInfo> querySkuPageByFilters(Map<String, Object> params) {
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
        SkuInfoExample skuInfoExample = new SkuInfoExample();
        SkuInfoExample.Criteria criteria = skuInfoExample.createCriteria();

        // 关键词检索
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andKeywordFilter(keyword);
        }

        // 分类id检索
        String catelogId = params.get("catelogId").toString();
        if (!StringUtils.isEmpty(catelogId) && !catelogId.equalsIgnoreCase("0")) {
            criteria.andCatalogIdEqualTo(Long.parseLong(catelogId));
        }

        // 品牌id检索
        String brandId = params.get("brandId").toString();
        if (!StringUtils.isEmpty(brandId) && !brandId.equalsIgnoreCase("0")) {
            criteria.andBrandIdEqualTo(Long.parseLong(brandId));
        }

        // 价格区间检索
        String min = params.get("min").toString();
        if (!StringUtils.isEmpty(min)) {
            criteria.andPriceGreaterThanOrEqualTo(new BigDecimal(min));
        }
        String max = params.get("max").toString();
        if (!StringUtils.isEmpty(max)) {
            BigDecimal maxValue = new BigDecimal(max);
            // max值只有大于0时候才生效加入筛选条件，不设置的话默认为0
            if (maxValue.compareTo(new BigDecimal("0")) == 1) {
                criteria.andPriceLessThanOrEqualTo(new BigDecimal(max));
            }
        }
        List<SkuInfo> skuInfos = skuInfoMapper.selectByExample(skuInfoExample);
        return CommonPageInfo.convertToCommonPage(skuInfos);
    }
}
