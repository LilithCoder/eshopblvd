package com.hatsukoi.eshopblvd.product.service.impl;

import com.hatsukoi.eshopblvd.product.dao.SpuInfoDescMapper;
import com.hatsukoi.eshopblvd.product.entity.SpuInfoDesc;
import com.hatsukoi.eshopblvd.product.service.SpuInfoDescService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gaoweilin
 * @date 2022/03/28 Mon 3:38 AM
 */
@Service
public class SpuInfoDescServiceImpl implements SpuInfoDescService {
    @Autowired
    SpuInfoDescMapper spuInfoDescMapper;

    @Override
    public void insertSpuInfoDesc(SpuInfoDesc spuInfoDesc) {
        spuInfoDescMapper.insert(spuInfoDesc);
    }
}
