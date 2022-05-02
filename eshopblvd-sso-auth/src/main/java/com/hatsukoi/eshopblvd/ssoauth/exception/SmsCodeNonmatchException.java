package com.hatsukoi.eshopblvd.ssoauth.exception;

/**
 * @author gaoweilin
 * @date 2022/04/28 Thu 4:09 AM
 */
public class SmsCodeNonmatchException extends RuntimeException{
    public SmsCodeNonmatchException() {
        super("验证码匹配错误");
    }
}
