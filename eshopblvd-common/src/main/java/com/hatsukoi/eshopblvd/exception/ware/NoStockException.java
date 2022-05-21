package com.hatsukoi.eshopblvd.exception.ware;

/**
 * @author gaoweilin
 * @date 2022/05/19 Thu 10:52 PM
 */
public class NoStockException extends RuntimeException{
    public NoStockException(Long skuId) {
        super("商品" + skuId + "没有足够库存了");
    }

    public NoStockException(String msg) {
        super(msg);
    }
}
