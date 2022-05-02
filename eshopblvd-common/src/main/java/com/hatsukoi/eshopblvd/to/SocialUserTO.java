package com.hatsukoi.eshopblvd.to;

import lombok.Data;
import java.io.Serializable;

/**
 * @author gaoweilin
 * 接口/oauth2/access_token的返回数据模型
 * 接口wiki：https://open.weibo.com/wiki/Oauth2/access_token
 * @date 2022/05/01 Sun 4:45 PM
 */
@Data
public class SocialUserTO implements Serializable {
    /**
     * 微博API访问令牌
     * 用户授权的唯一票据，用于调用微博的开放接口
     * 第三方应用应该用该票据和自己应用内的用户建立唯一影射关系，来识别登录状态
     */
    private String access_token;
    /**
     * access_token的生命周期（该参数即将废弃，开发者请使用expires_in）
     */
    private String remind_in;
    /**
     * access_token的生命周期，单位是秒数
     */
    private String expires_in;
    /**
     * 授权用户的UID
     * 本字段只是为了方便开发者，减少一次user/show接口调用而返回的
     * 第三方应用不能用此字段作为用户登录状态的识别
     * 只有access_token才是用户授权的唯一票据
     */
    private String uid;
}

