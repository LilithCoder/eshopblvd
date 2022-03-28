package com.hatsukoi.eshopblvd.product.service.impl;

import com.hatsukoi.eshopblvd.product.dao.ProductAttrValueMapper;
import com.hatsukoi.eshopblvd.product.entity.ProductAttrValue;
import com.hatsukoi.eshopblvd.product.service.ProductAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
