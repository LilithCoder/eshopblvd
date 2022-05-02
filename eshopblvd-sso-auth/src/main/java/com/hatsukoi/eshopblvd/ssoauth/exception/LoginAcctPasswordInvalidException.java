package com.hatsukoi.eshopblvd.ssoauth.exception;

/**
 * @author gaoweilin
 * @date 2022/04/30 Sat 11:14 AM
 */
public class LoginAcctPasswordInvalidException extends RuntimeException{
    public LoginAcctPasswordInvalidException() {
        super("账号密码错误");
    }
}
