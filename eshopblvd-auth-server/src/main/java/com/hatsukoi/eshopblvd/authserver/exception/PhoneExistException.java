package com.hatsukoi.eshopblvd.authserver.exception;

/**
 * @author gaoweilin
 * @date 2022/04/28 Thu 4:26 AM
 */
public class PhoneExistException extends RuntimeException{
    public PhoneExistException() {
        super("手机号已被注册");
    }
}
