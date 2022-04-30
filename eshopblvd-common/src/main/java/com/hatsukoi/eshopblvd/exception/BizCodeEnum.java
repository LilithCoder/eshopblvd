package com.hatsukoi.eshopblvd.exception;

/**
 * 错误状态码枚举类
 * 错误码 + 错误信息
 * 1. 错误码定义规则为5为数字
 * 2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知异常
 * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 * 错误码列表：
 * 10: 通用
 * 001：参数格式校验
 * 002：短信验证码频率太高
 * 11: 商品
 * 12: 订单
 * 13: 购物车
 * 14: 物流
 * 15: 用户
 * 21：库存
 * @author gaoweilin
 * @date 2022/03/17 Thu 2:41 AM
 */
public enum BizCodeEnum {
    UNKOWN_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验失败"),
    SMS_CODE_EXCEPTION(10002,"验证码获取频率太高，稍后再试"), // 验证码获取
    SMS_CODE_NONMATCH_EXCEPTION(15001,"验证码匹配错误"), // 注册
    SMS_CODE_TIMEOUT_EXCEPTION(15002,"验证码过期或尚未获取验证码"), // 注册
    USER_EXIST_EXCEPTION(15003,"用户名已被占用"), // 注册
    PHONE_EXIST_EXCEPTION(15004,"手机号已被注册"), // 注册
    LOGINACCT_NONEXIST_EXCEPTION(15005,"该账号尚未注册"), // 登陆
    LOGINACCT_PASSWORD_INVAILD_EXCEPTION(15006,"账号密码错误"); // 登陆


    private int code;
    private String msg;
    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
