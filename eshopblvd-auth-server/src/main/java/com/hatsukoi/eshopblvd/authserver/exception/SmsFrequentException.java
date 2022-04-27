package com.hatsukoi.eshopblvd.authserver.exception;

/**
 * 验证码获取频率太高异常
 * @author gaoweilin
 * @date 2022/04/28 Thu 3:57 AM
 */
public class SmsFrequentException extends RuntimeException{
    public SmsFrequentException() {
        super("验证码获取频率太高，稍后再试");
    }
}
