package com.hatsukoi.eshopblvd.constant;

/**
 * 常量公共类
 * @author gaoweilin
 * @date 2022/03/22 Tue 1:53 AM
 */
public class ProductConstant {
    public enum AttrEnum {
        ATTR_TYPE_SALE(0, "sale"),
        ATTR_TYPE_BASE(1, "base");

        private int code;
        private String msg;

        AttrEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
        public int getCode() { return this.code; }
        public String getMsg() { return this.msg; }
    }
}
