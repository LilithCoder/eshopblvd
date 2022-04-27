package com.hatsukoi.eshopblvd.member.exception;

/**
 * 「手机号已存在」异常
 * @author gaoweilin
 * @date 2022/04/28 Thu 3:01 AM
 */
public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("该手机号已被注册");
    }
}
