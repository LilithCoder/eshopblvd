package com.hatsukoi.eshopblvd.product.service;

import com.hatsukoi.eshopblvd.product.entity.Brand;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;

import java.util.Map;

/**
 * 品牌业务逻辑
 *
 * @author gaoweilin
 * @date 2022/03/06 Sun 11:15 PM
 */
public interface BrandService {

    /**
     * 根据brandId获取品牌
     * （测试用）
     * @param brandId
     * @return
     */
    public Brand selectBrandById(long brandId);

    /**
     * 根据显示状态分页获取品牌
     * （测试用）
     * @param pageNum
     * @param pageSize
     * @return
     */
    public CommonPageInfo<Brand> queryBrandsByShowStatus(int pageNum, int pageSize, byte showStatus);

    CommonPageInfo<Brand> queryPageForBrands(Map<String, Object> params);

    int updateBrand(Brand brand);

    int updateStatus(Brand brand);
}
