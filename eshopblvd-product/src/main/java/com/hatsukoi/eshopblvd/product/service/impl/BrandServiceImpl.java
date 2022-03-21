package com.hatsukoi.eshopblvd.product.service.impl;

import com.github.pagehelper.PageHelper;
import com.hatsukoi.eshopblvd.product.dao.BrandMapper;
import com.hatsukoi.eshopblvd.product.entity.Brand;
import com.hatsukoi.eshopblvd.product.entity.BrandExample;
import com.hatsukoi.eshopblvd.product.service.BrandService;
import com.hatsukoi.eshopblvd.product.service.CategoryBrandRelationService;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

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
        // select * from pms_brand where name like %key% or brand_id = key
        BrandExample brandExample = new BrandExample();
        BrandExample.Criteria criteria1 = brandExample.createCriteria();
        // 关键词模糊查询品牌名
        if (!StringUtils.isEmpty(key)) {
            // sql like 通配符
            criteria1.andNameLike("%" + key + "%");
            if (StringUtils.isNumeric(key)) {
                // 关键字匹配brandId
                BrandExample.Criteria criteria2 = brandExample.createCriteria();
                criteria2.andBrandIdEqualTo(Long.parseLong(key));
                brandExample.or(criteria2);
            }
        }
        List<Brand> brandList = brandMapper.selectByExample(brandExample);
        return CommonPageInfo.convertToCommonPage(brandList);
    }

    @Transactional
    @Override
    public int updateBrand(Brand brand) {
        int count = brandMapper.updateByPrimaryKeySelective(brand);
        // 保证冗余字段的数据一致性
        if (!StringUtils.isEmpty(brand.getName())) {
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());
        }
        // TODO: 更新其他相关联的表，冗余存储
        return count;
    }

    /**
     * 更新品牌的显示状态
     * @param brand
     * @return
     */
    @Override
    public int updateStatus(Brand brand) {
        return brandMapper.updateByPrimaryKeySelective(brand);
    }

    /**
     * 根据品牌id查询品牌信息
     * @param brandId
     * @return
     */
    @Override
    public Brand getBrandById(Long brandId) {
        return brandMapper.selectByPrimaryKey(brandId);
    }

    @Override
    public void insertBrand(Brand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public void deleteByIds(Long[] brandIds) {
        BrandExample example = new BrandExample();
        example.createCriteria().andBrandIdIn(Arrays.asList(brandIds));
        brandMapper.deleteByExample(example);
    }

    @Override
    public List<Brand> getBrandByIds(List<Long> brandIds) {
        BrandExample example = new BrandExample();
        example.createCriteria().andBrandIdIn(brandIds);
        return brandMapper.selectByExample(example);
    }
}
