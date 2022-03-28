package com.hatsukoi.eshopblvd.coupon.service;

import com.hatsukoi.eshopblvd.to.SkuReductionTO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;

/**
 * @author gaoweilin
 * @date 2022/03/29 Tue 2:06 AM
 */
public interface SkuFullReductionService {
    CommonResponse insertSkuReduction(SkuReductionTO reductionTo);
}
