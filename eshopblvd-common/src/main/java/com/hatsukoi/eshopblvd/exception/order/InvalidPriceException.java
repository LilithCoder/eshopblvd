package com.hatsukoi.eshopblvd.exception.order;

/**
 * @author gaoweilin
 * @date 2022/05/19 Thu 1:22 PM
 */
public class InvalidPriceException extends RuntimeException{
    public InvalidPriceException() {
        super("订单验价失败，购物车有变动");
    }
}
