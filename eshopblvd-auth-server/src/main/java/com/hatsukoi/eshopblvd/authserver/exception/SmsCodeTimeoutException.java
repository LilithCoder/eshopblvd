package com.hatsukoi.eshopblvd.authserver.exception;

/**
 * @author gaoweilin
 * @date 2022/04/28 Thu 4:08 AM
 */
public class SmsCodeTimeoutException extends RuntimeException{
    public SmsCodeTimeoutException() {
        super("验证码过期或尚未获取验证码");
    }
}
