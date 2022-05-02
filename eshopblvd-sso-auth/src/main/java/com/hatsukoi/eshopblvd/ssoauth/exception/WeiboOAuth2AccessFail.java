package com.hatsukoi.eshopblvd.ssoauth.exception;

/**
 * @author gaoweilin
 * @date 2022/05/01 Sun 4:56 PM
 */
public class WeiboOAuth2AccessFail extends RuntimeException{
    public WeiboOAuth2AccessFail() {
        super("微博访问权限令牌access_token获取失败");
    }
}
