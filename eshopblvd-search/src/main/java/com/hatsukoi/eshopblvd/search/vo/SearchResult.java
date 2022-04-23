package com.hatsukoi.eshopblvd.search.vo;

import com.hatsukoi.eshopblvd.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * 主搜检索请求结果数据封装
 * @author gaoweilin
 * @date 2022/04/23 Sat 12:29 PM
 */
@Data
public class SearchResult {

    /**
     * 【筛选栏】当前查询到的结果，所有涉及到的品牌
     */
    private List<BrandVo> brands;
    /**
     * 【筛选栏】当前查询到的结果，所有涉及到的所有分类
     */
    private List<CatalogVo> catalogs;
    /**
     * 【筛选栏】当前查询到的结果，所有涉及到的所有属性
     */
    private List<AttrVo> attrs;
    /**
     * 【搜索结果】查询到的所有商品sku
     */
    private List<SkuEsModel> products;
    /**
     * 【分页】当前页码
     */
    private Integer pageNum;
    /**
     * 【分页】总页码
     */
    private Integer totalPages;
    /**
     * 【分页】总记录数
     */
    private Long total;

    /**
     * 品牌筛选项VO
     */
    @Data
    public static class BrandVo{
        /**
         * 品牌id
         */
        private Long brandId;
        /**
         * 品牌名
         */
        private String brandName;
        /**
         * 品牌图
         */
        private String brandImg;
    }
    /**
     * 分类筛选项VO
     */
    @Data
    public static class CatalogVo{
        /**
         * 分类id
         */
        private Long catalogId;
        /**
         * 分类名
         */
        private String catalogName;
    }
    /**
     * 属性筛选项VO
     */
    @Data
    public static class AttrVo{
        /**
         * 属性id
         */
        private Long attrId;
        /**
         * 属性名
         */
        private String attrName;
        /**
         * 属性值列表
         */
        private List<String> attrValue;
    }

}
