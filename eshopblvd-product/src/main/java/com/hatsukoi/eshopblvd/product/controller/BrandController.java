package com.hatsukoi.eshopblvd.product.controller;

import com.hatsukoi.eshopblvd.product.entity.Brand;
import com.hatsukoi.eshopblvd.product.service.BrandService;
import com.hatsukoi.eshopblvd.utils.CommonPageInfo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import com.hatsukoi.eshopblvd.valid.AddGroup;
import com.hatsukoi.eshopblvd.valid.UpdateGroup;
import com.hatsukoi.eshopblvd.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 品牌管理控制器
 * 对应数据库：pms_brand
 * @author gaoweilin
 * @date 2022/03/14 Mon 10:59 PM
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    BrandService brandService;

    /**
     * 新增一个品牌
     * @param brand
     * @return
     */
    @RequestMapping("/insert")
    public CommonResponse insert(@Validated({AddGroup.class}) @RequestBody Brand brand) {
        brandService.insertBrand(brand);
        return CommonResponse.success();
    }

    @RequestMapping("/batchDelete")
    public CommonResponse batchDelete(@RequestBody Long[] brandIds) {
        brandService.deleteByIds(brandIds);
        return CommonResponse.success();
    }

    /**
     * 更新品牌信息
     * @param brand
     * @return
     */
    @RequestMapping("/update")
    public CommonResponse update(@Validated(UpdateGroup.class) @RequestBody Brand brand) {
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
    public CommonResponse updateStatus(@Validated(UpdateStatusGroup.class) @RequestBody Brand brand) {
        int count = brandService.updateStatus(brand);
        if (count > 0) {
            return CommonResponse.success();
        } else {
            return CommonResponse.error();
        }
    }

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
     * 根据brandId获取指定品牌的信息
     * @param brandId
     * @return
     */
    @RequestMapping("/info/{brandId}")
    public CommonResponse brandInfo(@PathVariable("brandId") Long brandId) {
        Brand brand = brandService.getBrandById(brandId);
        return CommonResponse.success().setData(brand);
    }

    /**
     * 根据brandId列表批量查询
     * @param brandIds
     * @return
     */
    @RequestMapping("infos")
    public CommonResponse brandsInfo(@RequestParam("brandIds") List<Long> brandIds) {
        List<Brand> brands = brandService.getBrandByIds(brandIds);
        return CommonResponse.success().setData(brands);
    }

}
