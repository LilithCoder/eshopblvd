package com.hatsukoi.eshopblvd.ssoauth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * 用户注册vo数据模型
 * @author gaoweilin
 * @date 2022/04/28 Thu 2:07 AM
 */
@Data
public class UserRegisterVO {
    /**
     * 用户名
     */
    @NotEmpty(message = "用户不能为空")
    @Length(min = 6, max = 18, message = "用户名必须是6-18位字符")
    private String userName;
    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 18, message = "密码必须是6-18位字符")
    private String password;
    /**
     * 手机号
     */
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")
    private String phone;
    /**
     * 验证码
     */
    @NotEmpty(message = "验证码不能为空")
    private String code;
}
