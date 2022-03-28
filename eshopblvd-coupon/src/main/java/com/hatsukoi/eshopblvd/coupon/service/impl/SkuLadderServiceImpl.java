package com.hatsukoi.eshopblvd.coupon.service.impl;

import com.hatsukoi.eshopblvd.coupon.dao.SkuLadderMapper;
import com.hatsukoi.eshopblvd.coupon.entity.SkuLadder;
import com.hatsukoi.eshopblvd.coupon.service.SkuLadderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gaoweilin
 * @date 2022/03/29 Tue 2:12 AM
 */
@Service
public class SkuLadderServiceImpl implements SkuLadderService {
    @Autowired
    SkuLadderMapper skuLadderMapper;

    @Override
    public void insert(SkuLadder skuLadder) {
        skuLadderMapper.insert(skuLadder);
    }
}
