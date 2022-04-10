package com.hatsukoi.eshopblvd.to.es;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * ElasticSearch中存储的上架商品sku数据模型
 * @author gaoweilin
 * @date 2022/04/09 Sat 3:07 PM
 */
@Data
public class SkuEsModel implements Serializable {
    private Long skuId;

    private Long spuId;
    /**
     * 标题
     */
    private String skuTitle;
    /**
     * 价格
     */
    private BigDecimal skuPrice;
    /**
     * sku默认头图
     */
    private String skuImg;
    /**
     * 销量
     */
    private Long saleCount;
    /**
     * 是否有库存
     */
    private Boolean hasStock;
    /**
     * 热度评分（访问量）
     */
    private Long hotScore;

    private Long brandId;

    private Long catalogId;

    private String brandName;

    private String brandImg;

    private String catalogName;

    /**
     * 可以被检索的规格参数
     */
    private List<Attrs> attrs;

    @Data
    public static class Attrs implements Serializable {

        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
