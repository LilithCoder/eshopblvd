package com.hatsukoi.eshopblvd.product.service;

import com.hatsukoi.eshopblvd.product.entity.SpuInfo;
import com.hatsukoi.eshopblvd.product.vo.SpuInsertVO;

/**
 * @author gaoweilin
 * @date 2022/03/28 Mon 3:13 AM
 */
public interface SpuInfoService {
    void insertNewSpu(SpuInsertVO vo);

    void insertBaseSpuInfo(SpuInfo spuInfo);
}
