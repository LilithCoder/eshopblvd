package com.hatsukoi.eshopblvd.coupon.service.impl;

import com.hatsukoi.eshopblvd.api.coupon.CouponRpcService;
import com.hatsukoi.eshopblvd.coupon.dao.SeckillSessionMapper;
import com.hatsukoi.eshopblvd.coupon.dao.SeckillSkuRelationMapper;
import com.hatsukoi.eshopblvd.coupon.entity.SeckillSession;
import com.hatsukoi.eshopblvd.coupon.entity.SeckillSessionExample;
import com.hatsukoi.eshopblvd.coupon.entity.SeckillSkuRelation;
import com.hatsukoi.eshopblvd.coupon.entity.SeckillSkuRelationExample;
import com.hatsukoi.eshopblvd.to.SeckillSessionTo;
import com.hatsukoi.eshopblvd.to.SeckillSkuRelationTo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gaoweilin
 * @date 2022/05/27 Fri 10:50 AM
 */
public class CouponRpcServiceImpl implements CouponRpcService {
    @Autowired
    private SeckillSessionMapper seckillSessionMapper;

    @Autowired
    private SeckillSkuRelationMapper seckillSkuRelationMapper;

    @Override
    public CommonResponse scanNext3DaysSeckillSession() {
        // 秒杀场次扫描开始/结束时间
        String start = startTime();
        String end = endTime();

        // 根据秒杀场次的开始时间来获取场次
        SeckillSessionExample seckillSessionExample = new SeckillSessionExample();
        seckillSessionExample.createCriteria().andStartTimeBetween(start, end);
        List<SeckillSession> seckillSessions = seckillSessionMapper.selectByExample(seckillSessionExample);

        // 封装数据，返回查到的所有秒杀场次以及对应的商品
        if (seckillSessions != null && seckillSessions.size() > 0) {
            List<SeckillSessionTo> collect = seckillSessions.stream().map(session -> {
                SeckillSessionTo sessionTo = new SeckillSessionTo();
                BeanUtils.copyProperties(session, sessionTo);
                // 查找每个秒杀活动关联的商品，封装数据
                SeckillSkuRelationExample example = new SeckillSkuRelationExample();
                example.createCriteria().andPromotionSessionIdEqualTo(session.getId());
                List<SeckillSkuRelation> seckillSkuRelations = seckillSkuRelationMapper.selectByExample(example);
                List<SeckillSkuRelationTo> relations = seckillSkuRelations.stream().map(relation -> {
                    SeckillSkuRelationTo seckillSkuRelation = new SeckillSkuRelationTo();
                    BeanUtils.copyProperties(relation, seckillSkuRelation);
                    return seckillSkuRelation;
                }).collect(Collectors.toList());
                sessionTo.setSessionSkuRelations(relations);
                return sessionTo;
            }).collect(Collectors.toList());
            return CommonResponse.success().setData(collect);
        }
        return CommonResponse.error("无秒杀活动可上线");
    }

    /**
     * 秒杀场次扫描开始时间
     * 当天00:00:00
     */
    private String startTime() {
        LocalDate now = LocalDate.now();
        LocalTime min = LocalTime.MIN;
        LocalDateTime start = LocalDateTime.of(now, min);
        return start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 秒杀场次扫描结束时间
     * 后天23:59:59
     */
    private String endTime() {
        LocalDate now = LocalDate.now();
        LocalTime max = LocalTime.MAX;
        LocalDateTime end = LocalDateTime.of(now.plusDays(2), max);
        return end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
