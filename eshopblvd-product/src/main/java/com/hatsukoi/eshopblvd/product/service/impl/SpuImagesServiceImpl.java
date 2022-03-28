package com.hatsukoi.eshopblvd.product.service.impl;

import com.hatsukoi.eshopblvd.product.dao.SpuImagesMapper;
import com.hatsukoi.eshopblvd.product.entity.SpuImages;
import com.hatsukoi.eshopblvd.product.service.SpuImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/03/28 Mon 3:44 AM
 */
@Service
public class SpuImagesServiceImpl implements SpuImagesService {
    @Autowired
    SpuImagesMapper spuImagesMapper;

    /**
     * 插入spu对应的图集
     * spu可以对应多个图
     * @param spuId
     * @param images
     */
    @Override
    public void insertImages(Long spuId, List<String> images) {
        if (images != null && images.size() != 0) {
            List<SpuImages> collect = images.stream().map((img) -> {
                SpuImages spuImages = new SpuImages();
                spuImages.setSpuId(spuId);
                spuImages.setImgUrl(img);
                return spuImages;
            }).collect(Collectors.toList());
            spuImagesMapper.batchInsert(collect);
        } else {
            return;
        }
    }
}
