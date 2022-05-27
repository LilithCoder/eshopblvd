package com.hatsukoi.eshopblvd.seckill.controller;

import com.hatsukoi.eshopblvd.seckill.service.SeckillService;
import com.hatsukoi.eshopblvd.seckill.to.SeckillSkuRedisTo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * @author gaoweilin
 * @date 2022/05/27 Fri 10:27 AM
 */
@Slf4j
@Controller
public class SeckillController {
    @Autowired
    private SeckillService seckillService;

    /**
     * 返回当前时间可以参与的秒杀商品信息
     * @return
     */
    public CommonResponse getCurrentSeckillSkus() {
        List<SeckillSkuRedisTo> data = seckillService.getCurrentSeckillSkus();
        log.info("获取当前秒杀活动");
        return CommonResponse.success().setData(data);
    }
}
