package com.hatsukoi.eshopblvd.product.service.impl;

import com.hatsukoi.eshopblvd.product.dao.CategoryMapper;
import com.hatsukoi.eshopblvd.product.entity.Category;
import com.hatsukoi.eshopblvd.product.entity.CategoryExample;
import com.hatsukoi.eshopblvd.product.service.CategoryBrandRelationService;
import com.hatsukoi.eshopblvd.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 查出所有的分类以及其子分类，并且以父子树形结构组装起来
     * @return
     */
    @Override
    public List<Category> getCategoryTree() {
        // TODO: 查询时只查showStatus为1的分类
        // 查出所有分类
        List<Category> allCategories = this.categoryMapper.selectByExample(null);
        // 从所有一级分类开始查找并组装其子分类
        List<Category> level1Categories = allCategories.stream()
                .filter(category -> {
                    return category.getCatLevel() == 1;
                }).map(category -> {
                    category.setChildren(getChildren(category, allCategories));
                    return category;
                }).sorted((cat1, cat2) -> {
                    // 子分类排序
                    Integer sort1 = cat1.getSort() != null ? cat1.getSort() : 0;
                    Integer sort2 = cat2.getSort() != null ? cat2.getSort() : 0;
                    return sort1.compareTo(sort2);
                }).collect(Collectors.toList());
        return level1Categories;
    }

    /**
     * 根据分类id查询
     * @param catId
     * @return
     */
    @Override
    public Category getCategoryById(Long catId) {
        return categoryMapper.selectByPrimaryKey(catId);
    }

    @Override
    public int removeCategoriesByIds(List<Long> catIds) {
        // TODO: 先检查当前删除的分类是否已经没有子分类或者是否被其他地方引用，没有才可以删
        // 根据catIds批量删除分类
        CategoryExample example = new CategoryExample();
        example.createCriteria().andCatIdIn(catIds);
        return categoryMapper.deleteByExample(example);
    }

    /**
     * 插入指定分类
     * @param category
     */
    @Override
    public int insertCategory(Category category) {
        return categoryMapper.insert(category);
    }

    /**
     * 根据catId去更新指定分类内容
     * @param category
     */
    @Override
    @Transactional
    public void updateCategory(Category category) {
        // updateByPrimaryKeySelective不同，当某一实体类的属性为null时，mybatis会使用动态sql过滤掉，不更新该字段
        categoryMapper.updateByPrimaryKeySelective(category);
        // 级联修改
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     * 批量更新分类
     * @param categories
     */
    @Transactional
    @Override
    public void batchUpdateCategories(List<Category> categories) {
        // TODO: 批量更新这些待优化，用xml写sql
        for (Category category: categories) {
            updateCategory(category);
        }
    }

    /**
     * 递归查询分类路径
     * @param catelogId
     * @return [2, 34, 225]
     */
    @Override
    public Long[] getCatelogPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        findPath(catelogId, path);
        Collections.reverse(path);
        return path.toArray(new Long[path.size()]);
    }

    /**
     * 递归辅助函数
     * 查找父分类，记录在path里
     * @param catelogId
     * @param path
     */
    private void findPath(Long catelogId, List<Long> path) {
        if (catelogId == 0) return;
        path.add(catelogId);
        Category category = categoryMapper.selectByPrimaryKey(catelogId);
        findPath(category.getParentCid(), path);
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
