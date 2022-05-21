package com.hatsukoi.eshopblvd.order.controller;

import com.hatsukoi.eshopblvd.exception.BizCodeEnum;
import com.hatsukoi.eshopblvd.exception.order.InvalidPriceException;
import com.hatsukoi.eshopblvd.exception.order.OrderTokenException;
import com.hatsukoi.eshopblvd.exception.ware.NoStockException;
import com.hatsukoi.eshopblvd.order.entity.Order;
import com.hatsukoi.eshopblvd.order.service.OrderService;
import com.hatsukoi.eshopblvd.order.vo.OrderConfirmVO;
import com.hatsukoi.eshopblvd.order.vo.OrderSubmitVO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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

    /**
     * 提交订单
     * @param orderSubmit
     * @return
     */
    @PostMapping("/submitOrder")
    public CommonResponse submitOrder(OrderSubmitVO orderSubmit) {
        try {
            Order order = orderService.submitOrder(orderSubmit);
        } catch (OrderTokenException e) {
            // 放重令牌校验失败，重定向到订单确认页
            return CommonResponse.error(BizCodeEnum.ORDER_TOKEN_EXCEPTION.getCode(), BizCodeEnum.ORDER_TOKEN_EXCEPTION.getMsg());
        } catch (InvalidPriceException e) {
            // 订单验价失败，重定向到订单确认页
            return CommonResponse.error(BizCodeEnum.INVALID_PRICE_EXCEPTION.getCode(), BizCodeEnum.INVALID_PRICE_EXCEPTION.getMsg());
        } catch (NoStockException e) {
            // 库存锁定失败，商品库存不足，重定向到订单确认页
            return CommonResponse.error(BizCodeEnum.NO_STOCK_EXCEPTION.getCode(), BizCodeEnum.NO_STOCK_EXCEPTION.getMsg());
        }
        return CommonResponse.success();
    }
}
