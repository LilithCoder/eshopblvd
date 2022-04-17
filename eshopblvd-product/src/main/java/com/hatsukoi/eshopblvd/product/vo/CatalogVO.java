package com.hatsukoi.eshopblvd.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 在首页展示三级分类的数据
 * @author gaoweilin
 * @date 2022/04/11 Mon 12:48 AM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatalogVO {
    private Long catalog1Id;
    private String catalog1Name;
    private List<Catalog2VO> catalog2list;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catalog2VO {
        private Long catalog2Id;
        private String catalog2Name;
        private List<Catalog3VO> catalog3list;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catalog3VO {
        private Long catalog3Id;
        private String catalog3Name;
    }
}




