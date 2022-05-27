package com.hatsukoi.eshopblvd.seckill.schedule;

import com.hatsukoi.eshopblvd.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 秒杀商品定时任务
 * @author gaoweilin
 * @date 2022/05/27 Fri 10:29 AM
 */
@Slf4j
@Service
public class SeckillSchedule {
    @Autowired
    private SeckillService seckillService;

    /**
     * 秒杀定时上架任务
     * 每天晚上3点，上架最近三天的秒杀场次以及对应商品
     * 当天00:00:00 - 23:59:59
     * 明天00:00:00 - 23:59:59
     * 后天00:00:00 - 23:59:59
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void uploadSeckillNext3Days() {
        seckillService.uploadSeckillNext3Days();
    }
}
