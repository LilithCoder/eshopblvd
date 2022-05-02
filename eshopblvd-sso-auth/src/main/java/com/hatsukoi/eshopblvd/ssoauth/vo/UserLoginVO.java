package com.hatsukoi.eshopblvd.ssoauth.vo;

import lombok.Data;

/**
 * 用户登陆VO数据模型
 * @author gaoweilin
 * @date 2022/04/30 Sat 10:03 AM
 */
@Data
public class UserLoginVO {
    /**
     * 登陆账号(用户名/手机号)
     */
    public String userAcc;
    /**
     * 密码
     */
    public String password;
}
