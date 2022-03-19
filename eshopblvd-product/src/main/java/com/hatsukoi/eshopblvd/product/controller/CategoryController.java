package com.hatsukoi.eshopblvd.product.controller;

import com.hatsukoi.eshopblvd.product.entity.Category;
import com.hatsukoi.eshopblvd.product.service.CategoryService;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 * 对应数据库：pms_category
 * @author gaoweilin
 * @date 2022/03/13 Sun 1:09 PM
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @RequestMapping("/insert")
    public CommonResponse saveCategory(@RequestBody Category category) {
        int count = categoryService.insertCategory(category);
        if (count > 0) {
            return CommonResponse.success();
        } else {
            return CommonResponse.error();
        }
    }

    /**
     * 根据catId数组批量删除分类
     * @param catIds
     * @return
     */
    @RequestMapping("/deleteByIds")
    public CommonResponse deleteCategories(@RequestBody List<Long> catIds) {
        int count = categoryService.removeCategoriesByIds(catIds);
        if (count > 0) {
            return CommonResponse.success();
        } else {
            return CommonResponse.error();
        }
    }



    /**
     * 根据catId去更新指定分类内容
     * @param category
     * @return
     */
    @RequestMapping("update")
    public CommonResponse updateCategory(@RequestBody Category category) {
        int count = categoryService.updateCategory(category);
        if (count > 0) {
            return CommonResponse.success();
        } else {
            return CommonResponse.error();
        }
    }

    /**
     * 批量更新三级分类
     * @param categories
     * @return
     */
    @RequestMapping("batchUpdate")
    public CommonResponse batchUpdateCategories(@RequestBody List<Category> categories) {
        System.out.println(categories);
        int count = categoryService.batchUpdateCategories(categories);
        if (count > 0) {
            return CommonResponse.success();
        } else {
            return CommonResponse.error();
        }
    }

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
     * 根据分类id查询
     * @param catId
     * @return
     */
    @RequestMapping("/detail/{catId}")
    public CommonResponse getCategoryDetail(@PathVariable("catId") Long catId) {
        Category category = categoryService.getCategoryById(catId);
        Map<String, Category> result = new HashMap<>();
        result.put("categoryDetail", category);
        return CommonResponse.success().setData(result);
    }

}
