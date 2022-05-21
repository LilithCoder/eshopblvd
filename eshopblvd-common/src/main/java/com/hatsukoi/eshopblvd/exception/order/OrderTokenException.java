package com.hatsukoi.eshopblvd.exception.order;

/**
 * @author gaoweilin
 * @date 2022/05/18 Wed 1:34 PM
 */
public class OrderTokenException extends RuntimeException{
    public OrderTokenException() {
        super("订单提交令牌验证失败");
    }
}
