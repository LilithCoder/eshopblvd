package com.hatsukoi.eshopblvd.product.controller;

import com.hatsukoi.eshopblvd.product.entity.CategoryBrandRelation;
import com.hatsukoi.eshopblvd.product.service.CategoryBrandRelationService;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌分类关联
 * 对应数据库：pms_category_brand_relation
 * @author gaoweilin
 * @date 2022/03/20 Sun 5:34 AM
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 保存品牌和分类的关联
     * @param categoryBrandRelation
     * @return
     */
    @RequestMapping("/insert")
    public CommonResponse insert(@RequestBody CategoryBrandRelation categoryBrandRelation) {
        categoryBrandRelationService.insertCategoryBrandRelation(categoryBrandRelation);
        return CommonResponse.success();
    }

    @RequestMapping("/batchDelete")
    public CommonResponse batchDelete(@RequestBody Long[] ids) {
        categoryBrandRelationService.batchDeleteByIds(ids);
        return CommonResponse.success();
    }

    /**
     * 查询某品牌下关联的所有分类
     * @param brandId
     * @return
     */
    @GetMapping("/catelog/list")
    public CommonResponse getCatelogList(@RequestParam("brandId") Long brandId) {
        List<CategoryBrandRelation> categoryBrandRelations = categoryBrandRelationService.getCatelogListById(brandId);
        return CommonResponse.success().setData(categoryBrandRelations);
    }
}
