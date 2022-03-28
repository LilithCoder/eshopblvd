package com.hatsukoi.eshopblvd.product.service;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/03/28 Mon 3:44 AM
 */
public interface SpuImagesService {
    void insertImages(Long spuId, List<String> images);
}
