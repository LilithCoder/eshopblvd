package com.hatsukoi.eshopblvd.member.exception;

/**
 * 「用户名已存在」异常
 * @author gaoweilin
 * @date 2022/04/28 Thu 3:02 AM
 */
public class UsernameExistException extends RuntimeException{
    public UsernameExistException() {
        super("该用户名已被占用");
    }
}
