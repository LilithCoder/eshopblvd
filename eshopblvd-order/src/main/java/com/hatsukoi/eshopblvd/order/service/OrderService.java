package com.hatsukoi.eshopblvd.order.service;

import com.hatsukoi.eshopblvd.order.vo.OrderConfirmVO;

import java.util.concurrent.ExecutionException;

/**
 * @author gaoweilin
 * @date 2022/05/09 Mon 1:25 AM
 */
public interface OrderService {
    OrderConfirmVO getOrderConfirmData(Long addrId) throws ExecutionException, InterruptedException;
}
