package com.hatsukoi.eshopblvd.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 主搜检索请求入参
 * @author gaoweilin
 * @date 2022/04/23 Sat 12:31 PM
 */
@Data
public class SearchParam {
    /**
     * 【筛选】检索关键词
     */
    private String keyword;
    /**
     * 【筛选】品牌id（可多选）
     */
    private List<Long> brandIds;
    /**
     * 【筛选】三级分类id
     */
    private Long catalog3Id;
    /**
     * 【筛选】属性参数（规格参数，可多选）
     * attrs=2_5寸:6寸&attrs=3_OLED:液晶屏
     * 选中2号属性的5寸和6寸，以及3号属性的OLED和液晶屏
     */
    private List<String> attrs;
    /**
     * 【过滤】是否只显示库存
     * 0：无库存
     * 1：有库存
     */
    private Integer hasStock;
    /**
     * 【过滤】价格区间
     * 1_500：1到500
     * _500：500以内
     * 500_：500以上
     */
    private String skuPrice;
    /**
     * 【排序条件】
     * 热度排序：sort=hotScore_asc/desc
     * 销量排序：sort=saleCount_asc/desc
     * 价格排序：sort=skuPrice_asc/desc
     */
    private String sort;
    /**
     * 【分页】页码
     */
    private Integer pageNum = 1;
}
