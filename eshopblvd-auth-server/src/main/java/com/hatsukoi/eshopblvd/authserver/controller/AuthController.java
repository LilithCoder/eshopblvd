package com.hatsukoi.eshopblvd.authserver.controller;

import com.hatsukoi.eshopblvd.api.thirdparty.SmsSendRpcService;
import com.hatsukoi.eshopblvd.constant.AuthServerConstant;
import com.hatsukoi.eshopblvd.exception.BizCodeEnum;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 认证控制器：（注册&登陆&短信验证码）
 * @author gaoweilin
 * @date 2022/04/26 Tue 2:52 AM
 */
@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Reference(check = false, interfaceName = "com.hatsukoi.eshopblvd.api.thirdparty.SmsSendRpcService")
    private SmsSendRpcService smsSendRpcService;

    /**
     * 发送短信验证码接口
     * @param phone
     * @return
     */
    @GetMapping("/sms/sendcode")
    public CommonResponse sendSmsCode(@RequestParam("phone") String phone) {
        String key = AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone;

        // 1. 接口防刷
        String redisCode = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(redisCode)) {
            // 获取这条已经发送该手机号上且存入redis的记录
            // 获取这条记录存入redis的时间，如果大于60s，这个手机号就可以再获取一次验证码
            long timestamp = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - timestamp < AuthServerConstant.RECURRENT_TIME) {
                // 60s内不能再发验证码，返沪响应的错误码和错误信息
                return CommonResponse.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        // 2. 该手机号首次发送验证码或者是过了60s可以再次发验证码了
        String code = UUID.randomUUID().toString().substring(0, 5);
        // 值是验证码加上当前系统时间，这是为了之后防止的再次获取验证码时距离上次没有超过60s
        String codeValue = code + "_" + System.currentTimeMillis();
        // redis缓存验证码，加上有效时间10min，用户10min内要来注册成功，否则就失效了
        stringRedisTemplate.opsForValue().set(key, codeValue);

        // 3. RPC调用三方服务去给手机号发送验证码
        smsSendRpcService.sendCode(phone, code);
        return CommonResponse.success();
    }
}
