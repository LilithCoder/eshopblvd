package com.hatsukoi.eshopblvd.product.service.impl;

import com.hatsukoi.eshopblvd.product.dao.CategoryMapper;
import com.hatsukoi.eshopblvd.product.entity.Category;
import com.hatsukoi.eshopblvd.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/03/13 Sun 1:14 PM
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryMapper categoryMapper;

    /**
     * 查出所有的分类以及其子分类，并且以父子树形结构组装起来
     * @return
     */
    @Override
    public List<Category> getCategoryTree() {
        // 查出所有分类
        List<Category> allCategories = this.categoryMapper.selectByExample(null);
        // 从所有一级分类开始查找并组装其子分类
        List<Category> level1Categories = allCategories.stream()
                .filter(category -> {
                    return category.getCatLevel() == 1;
                }).map(category -> {
                    category.setChildren(getChildren(category, allCategories));
                    return category;
                }).collect(Collectors.toList());
        return level1Categories;
    }

    /**
     * 递归查询并组装子分类辅助函数
     * @param root 父分类
     * @param allCategories 所有分类
     * @return
     */
    private List<Category> getChildren(Category root, List<Category> allCategories) {
        // 在所有分类中查找到所有root分类的子分类，继续遍历递归找其子分类直到叶层级分类，并组装树结构
        // 边界条件：如果是叶层级分类，那么就在所有分类中查找不到其子分类了，所有不用处理
        // 最后返回属于一级分类下的子分类树结构，让一级去组装完成最后的树结构
        List<Category> children = allCategories.stream()
                .filter(category -> {
                    return category.getParentCid() == root.getCatId();
                }).map(subCategory -> {
                    // 子分类查找并组装属于自己的子分类
                    subCategory.setChildren(getChildren(subCategory, allCategories));
                    return subCategory;
                }).sorted((cat1, cat2) -> {
                    // 子分类排序
                    Integer sort1 = cat1.getSort() != null ? cat1.getSort() : 0;
                    Integer sort2 = cat2.getSort() != null ? cat2.getSort() : 0;
                    return sort1.compareTo(sort2);
                }).collect(Collectors.toList());
        // 返回的是子分类们
        return children;
    }
}
