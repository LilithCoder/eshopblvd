package com.hatsukoi.eshopblvd.product.vo;

import lombok.Data;

/**
 * @author gaoweilin
 * @date 2022/03/28 Mon 2:42 AM
 */
@Data
public class Image {
    /**
     * sku图地址
     */
    private String imgUrl;
    /**
     * sku默认图[0 - 不是默认图，1 - 是默认图]
     */
    private int defaultImg;

}
