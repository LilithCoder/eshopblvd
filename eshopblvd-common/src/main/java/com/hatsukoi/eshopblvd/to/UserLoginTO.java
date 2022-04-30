package com.hatsukoi.eshopblvd.to;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoweilin
 * @date 2022/04/30 Sat 10:12 AM
 */
@Data
public class UserLoginTO implements Serializable {
    /**
     * 登陆账号(用户名/手机号)
     */
    public String userAcc;
    /**
     * 密码
     */
    public String password;
}
