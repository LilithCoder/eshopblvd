package com.hatsukoi.eshopblvd.product.service;

import com.hatsukoi.eshopblvd.product.entity.Category;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/03/13 Sun 1:14 PM
 */
public interface CategoryService {
    List<Category> getCategoryTree();

    Category getCategoryById(Long catId);

    int removeCategoriesByIds(List<Long> catIds);

    int insertCategory(Category category);

    void updateCategory(Category category);

    void batchUpdateCategories(@Param("categories") List<Category> categories);

    Long[] getCatelogPath(Long catelogId);
}
