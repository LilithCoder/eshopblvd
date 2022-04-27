package com.hatsukoi.eshopblvd.to;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册的TO
 * （传给会员服务注册业务用的）
 * @author gaoweilin
 * @date 2022/04/28 Thu 2:45 AM
 */
@Data
public class MemberRegisterTO implements Serializable {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 手机号
     */
    private String phone;
}
