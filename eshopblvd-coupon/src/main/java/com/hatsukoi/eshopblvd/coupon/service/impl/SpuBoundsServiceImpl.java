package com.hatsukoi.eshopblvd.coupon.service.impl;

import com.hatsukoi.eshopblvd.coupon.dao.SpuBoundsMapper;
import com.hatsukoi.eshopblvd.coupon.entity.SpuBounds;
import com.hatsukoi.eshopblvd.coupon.service.SpuBoundsService;
import com.hatsukoi.eshopblvd.to.SpuBoundTO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.apache.http.HttpStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author gaoweilin
 * @date 2022/03/28 Mon 11:42 PM
 */
@Service
@org.apache.dubbo.config.annotation.Service(timeout = 6000)
public class SpuBoundsServiceImpl implements SpuBoundsService {

    @Autowired
    private SpuBoundsMapper spuBoundsMapper;

    @Override
    public HashMap insertSpuBounds(SpuBoundTO spuBoundTO) {
        SpuBounds spuBounds = new SpuBounds();
        BeanUtils.copyProperties(spuBoundTO, spuBounds);
        spuBoundsMapper.insert(spuBounds);
        HashMap res = new HashMap();
        res.put("code", HttpStatus.SC_OK);
        return res;
    }
}
