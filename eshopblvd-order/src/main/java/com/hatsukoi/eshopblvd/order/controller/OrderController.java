package com.hatsukoi.eshopblvd.order.controller;

import com.hatsukoi.eshopblvd.order.service.OrderService;
import com.hatsukoi.eshopblvd.order.vo.OrderConfirmVO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author gaoweilin
 * @date 2022/05/09 Mon 1:22 AM
 */
@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 返回订单确认页的数据
     * @param addrId
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @RequestMapping("/toTrade")
    public CommonResponse getOrderConfirmData(@RequestParam(value = "addrId", required = false) Long addrId) throws ExecutionException, InterruptedException {
        OrderConfirmVO orderConfirm = orderService.getOrderConfirmData(addrId);
        return CommonResponse.success().setData(orderConfirm);
    }
}
