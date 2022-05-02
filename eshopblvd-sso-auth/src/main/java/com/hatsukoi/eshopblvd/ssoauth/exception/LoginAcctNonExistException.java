package com.hatsukoi.eshopblvd.ssoauth.exception;

/**
 * @author gaoweilin
 * @date 2022/04/30 Sat 11:13 AM
 */
public class LoginAcctNonExistException extends RuntimeException {
    public LoginAcctNonExistException() {
        super("该账号尚未注册");
    }
}
