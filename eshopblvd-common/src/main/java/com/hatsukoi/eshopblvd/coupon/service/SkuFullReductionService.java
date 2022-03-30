package com.hatsukoi.eshopblvd.coupon.service;

import com.hatsukoi.eshopblvd.to.SkuReductionTO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;

import java.util.HashMap;

/**
 * @author gaoweilin
 * @date 2022/03/29 Tue 2:06 AM
 */
public interface SkuFullReductionService {
    HashMap insertSkuReduction(SkuReductionTO reductionTo);
}
