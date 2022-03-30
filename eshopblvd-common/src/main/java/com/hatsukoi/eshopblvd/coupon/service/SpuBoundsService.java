package com.hatsukoi.eshopblvd.coupon.service;

import com.hatsukoi.eshopblvd.to.SpuBoundTO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;

import java.util.HashMap;

/**
 * spu积分provider RPC接口
 * @author gaoweilin
 * @date 2022/03/28 Mon 11:35 PM
 */
public interface SpuBoundsService {
    public HashMap insertSpuBounds(SpuBoundTO spuBoundTO);
}
