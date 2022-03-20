package com.hatsukoi.eshopblvd.product.service;

import com.hatsukoi.eshopblvd.product.entity.CategoryBrandRelation;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/03/20 Sun 6:48 AM
 */
public interface CategoryBrandRelationService {
    List<CategoryBrandRelation> getCatelogListById(Long brandId);

    void insertCategoryBrandRelation(CategoryBrandRelation categoryBrandRelation);

    void batchDeleteByIds(Long[] ids);

    void updateBrand(Long brandId, String name);

    void updateCategory(Long catId, String name);
}
