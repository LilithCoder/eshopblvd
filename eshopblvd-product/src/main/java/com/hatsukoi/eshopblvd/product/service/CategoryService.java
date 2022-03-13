package com.hatsukoi.eshopblvd.product.service;

import com.hatsukoi.eshopblvd.product.entity.Category;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/03/13 Sun 1:14 PM
 */
public interface CategoryService {
    List<Category> getCategoryTree();
}
