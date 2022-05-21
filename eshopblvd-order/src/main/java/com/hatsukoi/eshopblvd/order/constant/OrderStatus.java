package com.hatsukoi.eshopblvd.order.constant;

public enum OrderStatus {
    CREATE_NEW(Byte.valueOf("0"),"待付款"),
    PAYED(Byte.valueOf("1"),"已付款"),
    SENDED(Byte.valueOf("2"),"已发货"),
    RECIEVED(Byte.valueOf("3"),"已完成"),
    CANCLED(Byte.valueOf("4"),"已取消"),
    INVALID(Byte.valueOf("5"),"无效订单"),
    SERVICING(Byte.valueOf("6"),"售后中"),
    SERVICED(Byte.valueOf("7"),"售后完成");
    private Byte code;
    private String msg;

    OrderStatus(Byte code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Byte getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}