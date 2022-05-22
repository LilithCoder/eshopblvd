package com.hatsukoi.eshopblvd.order.controller;

import com.alipay.api.AlipayApiException;
import com.hatsukoi.eshopblvd.exception.BizCodeEnum;
import com.hatsukoi.eshopblvd.exception.order.InvalidPriceException;
import com.hatsukoi.eshopblvd.exception.order.OrderTokenException;
import com.hatsukoi.eshopblvd.exception.ware.NoStockException;
import com.hatsukoi.eshopblvd.order.config.AlipayTemplate;
import com.hatsukoi.eshopblvd.order.entity.Order;
import com.hatsukoi.eshopblvd.order.service.OrderService;
import com.hatsukoi.eshopblvd.order.vo.OrderConfirmVO;
import com.hatsukoi.eshopblvd.order.vo.OrderSubmitVO;
import com.hatsukoi.eshopblvd.order.vo.PayVo;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

/**
 * @author gaoweilin
 * @date 2022/05/09 Mon 1:22 AM
 */
@Controller
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AlipayTemplate alipayTemplate;

    /**
     * 返回订单确认页的数据
     * @param addrId
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @ResponseBody
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
    @ResponseBody
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

    /**
     * 支付选择页（支付收银台）点击「支付宝支付」
     * 会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "payOrder", produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        // 获取当前支付需要的信息
        PayVo payVo = orderService.buildPayData(orderSn);
        String pay = alipayTemplate.pay(payVo);
        return pay;
    }
}
