package com.hatsukoi.eshopblvd.product.service.impl;

import com.hatsukoi.eshopblvd.product.dao.SkuSaleAttrValueMapper;
import com.hatsukoi.eshopblvd.product.entity.SkuSaleAttrValue;
import com.hatsukoi.eshopblvd.product.entity.SpuSaleAttrPO;
import com.hatsukoi.eshopblvd.product.service.SkuSaleAttrValueService;
import com.hatsukoi.eshopblvd.product.vo.SkuItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/03/29 Tue 1:40 AM
 */
@Service
public class SkuSaleAttrValueServiceImpl implements SkuSaleAttrValueService {
    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Override
    public void batchInsert(List<SkuSaleAttrValue> skuSaleAttrValues) {
        skuSaleAttrValueMapper.batchInsert(skuSaleAttrValues);
    }

    @Override
    public List<SpuSaleAttrPO> getSaleAttrsBySpuId(Long spuId) {
        List<SpuSaleAttrPO> saleAttrs = skuSaleAttrValueMapper.getSaleAttrsBySpuId(spuId);
        return saleAttrs;
    }
}
