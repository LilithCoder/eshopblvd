package com.hatsukoi.eshopblvd.product.service.impl;

import com.hatsukoi.eshopblvd.product.dao.SkuImagesMapper;
import com.hatsukoi.eshopblvd.product.entity.SkuImages;
import com.hatsukoi.eshopblvd.product.service.SkuImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/03/29 Tue 1:11 AM
 */
@Service
public class SkuImagesServiceImpl implements SkuImagesService {
    @Autowired
    SkuImagesMapper skuImagesMapper;

    @Override
    public void batchInsert(List<SkuImages> skuImages) {
        skuImagesMapper.batchInsertImgs(skuImages);
    }
}
