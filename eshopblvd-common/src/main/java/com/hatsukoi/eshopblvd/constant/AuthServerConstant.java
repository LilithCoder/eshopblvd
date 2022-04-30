package com.hatsukoi.eshopblvd.constant;

/**
 * @author gaoweilin
 * @date 2022/04/26 Tue 11:37 PM
 */
public class AuthServerConstant {
    /**
     * 验证码redis缓存key的前缀
     */
    public static final String SMS_CODE_CACHE_PREFIX = "sms:code:";
    /**
     * 同一手机号再次可以获取验证码的时间
     */
    public static final long RECURRENT_TIME = 60 * 1000;
    /**
     * 登陆成功后用户信息放入session对应的字段key
     */
    public static final String LOGIN_USER = "loginUser";
}
