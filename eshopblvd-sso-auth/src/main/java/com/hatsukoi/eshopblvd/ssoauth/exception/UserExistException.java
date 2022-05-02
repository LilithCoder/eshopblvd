package com.hatsukoi.eshopblvd.ssoauth.exception;

/**
 * @author gaoweilin
 * @date 2022/04/28 Thu 4:27 AM
 */
public class UserExistException extends RuntimeException{
    public UserExistException() {
        super("用户名已被占用");
    }
}
