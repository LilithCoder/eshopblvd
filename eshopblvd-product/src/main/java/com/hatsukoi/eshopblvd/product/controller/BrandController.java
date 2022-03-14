package com.hatsukoi.eshopblvd.product.controller;

import com.hatsukoi.eshopblvd.product.entity.Brand;
import com.hatsukoi.eshopblvd.product.service.BrandService;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 品牌管理控制器
 * @author gaoweilin
 * @date 2022/03/14 Mon 10:59 PM
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    BrandService brandService;

    /**
     * 根据关键词分页查询所有品牌
     * @param params
     * @return
     */
    @RequestMapping("/list")
    public CommonResponse list(@RequestParam Map<String, Object> params) {
        CommonPageInfo<Brand> queryPage = brandService.queryPageForBrands(params);
        return CommonResponse.success().setData(queryPage);
    }

    /**
     * 更新品牌信息
     * @param brand
     * @return
     */
    @RequestMapping("/update")
    public CommonResponse update(@RequestBody Brand brand) {
        int count = brandService.updateBrand(brand);
        if (count > 0) {
            return CommonResponse.success();
        } else {
            return CommonResponse.error();
        }
    }

    /**
     * 更新品牌显示状态
     */
    @RequestMapping("/update/status")
    public CommonResponse updateStatus(@RequestBody Brand brand) {
        int count = brandService.updateStatus(brand);
        if (count > 0) {
            return CommonResponse.success();
        } else {
            return CommonResponse.error();
        }
    }
}
