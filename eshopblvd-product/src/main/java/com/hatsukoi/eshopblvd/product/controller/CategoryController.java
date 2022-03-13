package com.hatsukoi.eshopblvd.product.controller;

import com.hatsukoi.eshopblvd.product.entity.Category;
import com.hatsukoi.eshopblvd.product.service.CategoryService;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 * @author gaoweilin
 * @date 2022/03/13 Sun 1:09 PM
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    /**
     * 查出所有的分类以及其子分类，并且以父子树形结构组装起来
     * @return
     */
    @RequestMapping("/list")
    public CommonResponse getAllCategories() {
        List<Category> list = categoryService.getCategoryTree();
        Map<String, List<Category>> result = new HashMap<>();
        result.put("categoryList", list);
        return CommonResponse.success().setData(result);
    }

    /**
     * 根据catId数组批量删除分类
     * @param catIds
     * @return
     */
    @RequestMapping("/delete")
    public CommonResponse deleteCategories(@RequestBody List<Long> catIds) {
        categoryService.removeCategoriesByIds(catIds);
        return CommonResponse.success();
    }

}
