package com.hatsukoi.eshopblvd.product.service.impl;

import com.hatsukoi.eshopblvd.product.dao.BrandMapper;
import com.hatsukoi.eshopblvd.product.dao.CategoryBrandRelationMapper;
import com.hatsukoi.eshopblvd.product.dao.CategoryMapper;
import com.hatsukoi.eshopblvd.product.entity.Brand;
import com.hatsukoi.eshopblvd.product.entity.Category;
import com.hatsukoi.eshopblvd.product.entity.CategoryBrandRelation;
import com.hatsukoi.eshopblvd.product.entity.CategoryBrandRelationExample;
import com.hatsukoi.eshopblvd.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/03/20 Sun 6:48 AM
 */
@Service
public class CategoryBrandRelationServiceImpl implements CategoryBrandRelationService {
    @Autowired
    CategoryBrandRelationMapper categoryBrandRelationMapper;

    @Autowired
    BrandMapper brandMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public List<CategoryBrandRelation> getCatelogListById(Long brandId) {
        CategoryBrandRelationExample example = new CategoryBrandRelationExample();
        example.createCriteria().andBrandIdEqualTo(brandId);
        List<CategoryBrandRelation> categoryBrandRelations = categoryBrandRelationMapper.selectByExample(example);
        return categoryBrandRelations;
    }

    @Override
    public void insertCategoryBrandRelation(CategoryBrandRelation categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        // 查询品牌和分类的名字
        Brand brand = brandMapper.selectByPrimaryKey(brandId);
        Category category = categoryMapper.selectByPrimaryKey(catelogId);
        // 补充查询到信息：品牌和分类的名字
        categoryBrandRelation.setBrandName(brand.getName());
        categoryBrandRelation.setCatelogName(category.getName());
        categoryBrandRelationMapper.insert(categoryBrandRelation);
    }

    @Override
    public void batchDeleteByIds(Long[] ids) {
        CategoryBrandRelationExample example = new CategoryBrandRelationExample();
        example.createCriteria().andIdIn(Arrays.asList(ids));
        categoryBrandRelationMapper.deleteByExample(example);
    }

    /**
     * 更新品牌的时候更新关联表的冗余字段
     * 根据brandId更新品牌名
     * @param brandId
     * @param name
     */
    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelation categoryBrandRelation = new CategoryBrandRelation();
        categoryBrandRelation.setBrandId(brandId);
        categoryBrandRelation.setBrandName(name);
        CategoryBrandRelationExample example = new CategoryBrandRelationExample();
        example.createCriteria().andBrandIdEqualTo(brandId);
        categoryBrandRelationMapper.updateByExampleSelective(categoryBrandRelation, example);
    }

    /**
     * 更新分类的时候更新关联表的冗余字段
     * 根据catId更新catelogName
     * @param catId
     * @param name
     */
    @Override
    public void updateCategory(Long catId, String name) {
        CategoryBrandRelation categoryBrandRelation = new CategoryBrandRelation();
        categoryBrandRelation.setCatelogId(catId);
        categoryBrandRelation.setCatelogName(name);
        CategoryBrandRelationExample example = new CategoryBrandRelationExample();
        example.createCriteria().andCatelogIdEqualTo(catId);
        categoryBrandRelationMapper.updateByExampleSelective(categoryBrandRelation, example);
    }
}
