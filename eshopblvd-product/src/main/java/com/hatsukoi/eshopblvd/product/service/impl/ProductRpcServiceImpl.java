package com.hatsukoi.eshopblvd.product.service.impl;

import com.hatsukoi.eshopblvd.api.product.ProductRpcService;
import com.hatsukoi.eshopblvd.product.dao.SkuInfoMapper;
import com.hatsukoi.eshopblvd.product.dao.SkuSaleAttrValueMapper;
import com.hatsukoi.eshopblvd.product.entity.SkuInfo;
import com.hatsukoi.eshopblvd.product.entity.SkuInfoExample;
import com.hatsukoi.eshopblvd.product.entity.SkuSaleAttrValue;
import com.hatsukoi.eshopblvd.to.SkuInfoTO;
import com.hatsukoi.eshopblvd.to.SkuPriceTO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/05/05 Thu 1:57 PM
 */
@Service
@org.apache.dubbo.config.annotation.Service
public class ProductRpcServiceImpl implements ProductRpcService {
    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Override
    public CommonResponse getSkuInfo(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        SkuInfoTO skuInfoTO = new SkuInfoTO();
        BeanUtils.copyProperties(skuInfo, skuInfoTO);
        return CommonResponse.success().setData(skuInfoTO);
    }

    @Override
    public CommonResponse getSkuSaleAttrsWithValue(Long skuId) {
        List<String> skuSaleAttrs = skuSaleAttrValueMapper.getSaleAttrsWithValueBySkuId(skuId);
        return CommonResponse.success().setData(skuSaleAttrs);
    }

    @Override
    public CommonResponse getSkusPrice(List<Long> skuIds) {
        SkuInfoExample skuInfoExample = new SkuInfoExample();
        skuInfoExample.createCriteria().andSkuIdIn(skuIds);
        List<SkuInfo> skuInfoList = skuInfoMapper.selectByExample(skuInfoExample);
        List<SkuPriceTO> collect = skuInfoList.stream().map(skuInfo -> {
            SkuPriceTO skuPriceTO = new SkuPriceTO();
            skuPriceTO.setSkuId(skuInfo.getSkuId());
            skuPriceTO.setPrice(skuInfo.getPrice());
            return skuPriceTO;
        }).collect(Collectors.toList());
        return CommonResponse.success().setData(collect);
    }
}
