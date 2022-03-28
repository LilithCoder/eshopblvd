package com.hatsukoi.eshopblvd.product.service.impl;

import com.hatsukoi.eshopblvd.product.dao.SkuInfoMapper;
import com.hatsukoi.eshopblvd.product.entity.SkuInfo;
import com.hatsukoi.eshopblvd.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gaoweilin
 * @date 2022/03/29 Tue 1:02 AM
 */
@Service
public class SkuInfoServiceImpl implements SkuInfoService {
    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Override
    public void insertSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insert(skuInfo);
    }
}
