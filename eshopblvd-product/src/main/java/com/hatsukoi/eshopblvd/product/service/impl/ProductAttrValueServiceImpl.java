package com.hatsukoi.eshopblvd.product.service.impl;

import com.hatsukoi.eshopblvd.product.dao.ProductAttrValueMapper;
import com.hatsukoi.eshopblvd.product.entity.ProductAttrValue;
import com.hatsukoi.eshopblvd.product.entity.ProductAttrValueExample;
import com.hatsukoi.eshopblvd.product.service.ProductAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/03/28 Mon 4:23 AM
 */
@Service
public class ProductAttrValueServiceImpl implements ProductAttrValueService {
    @Autowired
    ProductAttrValueMapper productAttrValueMapper;

    @Override
    public void insertProductAttrValue(List<ProductAttrValue> collect) {
        productAttrValueMapper.batchInsert(collect);
    }

    @Override
    public List<ProductAttrValue> selectBaseAttrListForSpu(Long spuId) {
        ProductAttrValueExample productAttrValueExample = new ProductAttrValueExample();
        ProductAttrValueExample.Criteria criteria = productAttrValueExample.createCriteria();
        criteria.andSpuIdEqualTo(spuId);
        List<ProductAttrValue> productAttrValues = productAttrValueMapper.selectByExample(productAttrValueExample);
        return productAttrValues;
    }

    @Override
    @Transactional
    public void updateSpuAttr(Long spuId, List<ProductAttrValue> productAttrValueList) {
        // 删除这个spuId之前对应的所有属性
        ProductAttrValueExample productAttrValueExample = new ProductAttrValueExample();
        ProductAttrValueExample.Criteria criteria = productAttrValueExample.createCriteria();
        criteria.andSpuIdEqualTo(spuId);
        productAttrValueMapper.deleteByExample(productAttrValueExample);
        // 批量插入属性
        List<ProductAttrValue> collect = productAttrValueList.stream().map(item -> {
            item.setSpuId(spuId);
            return item;
        }).collect(Collectors.toList());
        productAttrValueMapper.batchInsert(collect);
    }
}
