package com.hatsukoi.eshopblvd.product.service;

import com.hatsukoi.eshopblvd.product.entity.SpuInfoDesc;
import org.springframework.stereotype.Service;

/**
 * @author gaoweilin
 * @date 2022/03/28 Mon 3:37 AM
 */
public interface SpuInfoDescService {
    void insertSpuInfoDesc(SpuInfoDesc spuInfoDesc);

    SpuInfoDesc getSpuInfoDescById(Long spuId);
}
