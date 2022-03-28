package com.hatsukoi.eshopblvd.coupon.service.impl;

import com.hatsukoi.eshopblvd.coupon.dao.MemberPriceMapper;
import com.hatsukoi.eshopblvd.coupon.entity.MemberPrice;
import com.hatsukoi.eshopblvd.coupon.service.MemberPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/03/29 Tue 2:12 AM
 */
@Service
public class MemberPriceServiceImpl implements MemberPriceService {
    @Autowired
    MemberPriceMapper memberPriceMapper;

    @Override
    public void batchInsert(List<MemberPrice> memberPrices) {
        memberPriceMapper.batchInsert(memberPrices);
    }
}
