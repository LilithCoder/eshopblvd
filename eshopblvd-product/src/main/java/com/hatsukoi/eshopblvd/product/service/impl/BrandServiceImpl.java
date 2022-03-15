package com.hatsukoi.eshopblvd.product.service.impl;

import com.github.pagehelper.PageHelper;
import com.hatsukoi.eshopblvd.product.dao.BrandMapper;
import com.hatsukoi.eshopblvd.product.entity.Brand;
import com.hatsukoi.eshopblvd.product.entity.BrandExample;
import com.hatsukoi.eshopblvd.product.service.BrandService;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/03/06 Sun 11:16 PM
 */
@com.alibaba.dubbo.config.annotation.Service
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 测试用
     * @param brandId
     * @return
     */
    @Override
    public Brand selectBrandById(long brandId) {
        return brandMapper.selectByPrimaryKey(brandId);
    }

    /**
     * 测试用
     * @param pageNum
     * @param pageSize
     * @param showStatus
     * @return
     */
    @Override
    public CommonPageInfo<Brand> queryBrandsByShowStatus(int pageNum, int pageSize, byte showStatus) {
        PageHelper.startPage(pageNum, pageSize);
        BrandExample brandExample = new BrandExample();
        brandExample.createCriteria().andShowStatusEqualTo(showStatus);
        List<Brand> brandList = brandMapper.selectByExample(brandExample);
        return CommonPageInfo.convertToCommonPage(brandList);
    }

    /**
     * 分页查询品牌列表
     * 查询条件：关键字为brand_id或是模糊查询brand_name
     * @return
     */
    @Override
    public CommonPageInfo<Brand> queryPageForBrands(Map<String, Object> params) {
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
        BrandExample brandExample = new BrandExample();
        // 关键词模糊查询品牌名
        if (!StringUtils.isEmpty(key)) {
            brandExample.createCriteria().andNameLike(key);
        }
        // 关键字匹配brandId
        if (!key.equals("") && StringUtils.isNumeric(key)) {
            brandExample.or().andBrandIdEqualTo(Long.parseLong(key));
        }
        List<Brand> brandList = brandMapper.selectByExample(brandExample);
        return CommonPageInfo.convertToCommonPage(brandList);
    }

    @Transactional
    @Override
    public int updateBrand(Brand brand) {
        int count = brandMapper.updateByPrimaryKeySelective(brand);
        // TODO: 更新其他相关联的表
        return count;
    }

    @Override
    public int updateStatus(Brand brand) {
        return brandMapper.updateByPrimaryKeySelective(brand);
    }
}
