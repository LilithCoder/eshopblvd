package com.hatsukoi.eshopblvd.authserver.exception;

/**
 * @author gaoweilin
 * @date 2022/05/01 Sun 4:57 PM
 */
public class WeiboOAuth2RpcFail extends RuntimeException{
    public WeiboOAuth2RpcFail() {
        super("RPC会员服务登陆接口失败");
    }
}
