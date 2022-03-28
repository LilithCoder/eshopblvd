package com.hatsukoi.eshopblvd.coupon.service;

import com.hatsukoi.eshopblvd.coupon.entity.MemberPrice;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/03/29 Tue 2:12 AM
 */
public interface MemberPriceService {
    void batchInsert(List<MemberPrice> memberPrices);
}
