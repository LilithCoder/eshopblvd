package com.hatsukoi.eshopblvd.ssoauth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hatsukoi.eshopblvd.api.member.MemberService;
import com.hatsukoi.eshopblvd.api.thirdparty.SmsSendRpcService;
import com.hatsukoi.eshopblvd.ssoauth.exception.*;
import com.hatsukoi.eshopblvd.ssoauth.service.AuthService;
import com.hatsukoi.eshopblvd.ssoauth.vo.UserLoginVO;
import com.hatsukoi.eshopblvd.ssoauth.vo.UserRegisterVO;
import com.hatsukoi.eshopblvd.constant.AuthServerConstant;
import com.hatsukoi.eshopblvd.exception.BizCodeEnum;
import com.hatsukoi.eshopblvd.to.MemberRegisterTO;
import com.hatsukoi.eshopblvd.to.MemberTO;
import com.hatsukoi.eshopblvd.to.SocialUserTO;
import com.hatsukoi.eshopblvd.to.UserLoginTO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import com.hatsukoi.eshopblvd.utils.HttpUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author gaoweilin
 * @date 2022/04/28 Thu 3:50 AM
 */
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Reference(check = false, interfaceName = "com.hatsukoi.eshopblvd.api.thirdparty.SmsSendRpcService")
    private SmsSendRpcService smsSendRpcService;

    @Reference(check = false, interfaceName = "com.hatsukoi.eshopblvd.api.member.MemberService")
    private MemberService memberService;

    /**
     * 获取验证码
     * @param phone
     * @throws SmsFrequentException
     */
    @Override
    public void sendSmsCode(String phone) throws SmsFrequentException {
        String key = AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone;

        // 1. 接口防刷
        String redisCode = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(redisCode)) {
            // 获取这条已经发送该手机号上且存入redis的记录
            // 获取这条记录存入redis的时间，如果大于60s，这个手机号就可以再获取一次验证码
            long timestamp = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - timestamp < AuthServerConstant.RECURRENT_TIME) {
                // 60s内不能再发验证码，抛异常返沪响应的错误码和错误信息
                throw new SmsFrequentException();
            }
        }

        // 2. 该手机号首次发送验证码或者是过了60s可以再次发验证码了
        String code = UUID.randomUUID().toString().substring(0, 5);
        // 值是验证码加上当前系统时间，这是为了之后防止的再次获取验证码时距离上次没有超过60s
        String codeValue = code + "_" + System.currentTimeMillis();
        // redis缓存验证码，加上有效时间30min，用户30min内要来注册成功，否则就失效了
        stringRedisTemplate.opsForValue().set(key, codeValue, 30, TimeUnit.MINUTES);

        // 3. RPC调用三方服务去给手机号发送验证码
        smsSendRpcService.sendCode(phone, code);
    }

    /**
     * 用户注册
     * @param userRegisterVO
     */
    @Override
    public void register(UserRegisterVO userRegisterVO) throws SmsCodeNonmatchException, SmsCodeTimeoutException, PhoneExistException, UserExistException {
        // 1. 校验验证码
        String code = userRegisterVO.getCode();
        String phone = userRegisterVO.getPhone();
        String codeRedisKey = AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone;
        String codeRedisValue = stringRedisTemplate.opsForValue().get(codeRedisKey);
        if (!StringUtils.isEmpty(codeRedisValue)) {
            if (code.equals(codeRedisValue.split("_")[0])) {
                // 验证码验证通过，可以删了
                stringRedisTemplate.delete(codeRedisKey);
                // RPC调用会员服务来完成注册业务
                MemberRegisterTO memberRegisterTO = new MemberRegisterTO();
                BeanUtils.copyProperties(userRegisterVO, memberRegisterTO);
                CommonResponse response = CommonResponse.convertToResp(memberService.register(memberRegisterTO));
                if (response.getCode() == BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode()) {
                    // 手机号已被注册错误
                    throw new PhoneExistException();
                }
                if (response.getCode() == BizCodeEnum.USER_EXIST_EXCEPTION.getCode()) {
                    // 用户名已被占用错误
                    throw new UserExistException();
                }
            } else {
                // 验证码匹配错误
                throw new SmsCodeNonmatchException();
            }
        } else {
            // 验证码10min过期或者用户根本没有获取验证码
            throw new SmsCodeTimeoutException();
        }
    }

    /**
     * 用户登陆
     * @param userLoginVO
     * @param session
     */
    @Override
    public void login(UserLoginVO userLoginVO, HttpSession session) throws LoginAcctNonExistException, LoginAcctPasswordInvalidException {
        UserLoginTO userLoginTO = new UserLoginTO();
        BeanUtils.copyProperties(userLoginVO, userLoginTO);
        HashMap<String, Object> login = memberService.login(userLoginTO);
        CommonResponse commonResponse = CommonResponse.convertToResp(login);
        if (commonResponse.getCode() == HttpStatus.SC_OK) {
            MemberTO data = commonResponse.getData(new TypeReference<MemberTO>() {
            });
            session.setAttribute(AuthServerConstant.LOGIN_USER, data);
            session.setMaxInactiveInterval(AuthServerConstant.SESSION_MAX_INACTIVE_INTERVAL);
        } else if (commonResponse.getCode() == BizCodeEnum.LOGINACCT_NONEXIST_EXCEPTION.getCode()) {
            throw new LoginAcctNonExistException();
        } else if (commonResponse.getCode() == BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode()) {
            throw new LoginAcctPasswordInvalidException();
        }
    }

    /**
     * 微博oauth2.0登陆逻辑
     * @param code
     * @param session
     * @param response
     */
    @Override
    public void weiboLogin(String code, HttpSession session, HttpServletResponse response) throws Exception, WeiboOAuth2RpcFail, WeiboOAuth2AccessFail {
        // 1. 授权成功跳转后得到了code，用这个去获取access_token和uid
        // 接口wiki：https://open.weibo.com/wiki/Oauth2/access_token
        Map<String, String> requestBody = new HashMap<>();
        // 申请应用时分配的AppKey
        // TODO: 等审核通过后需要修改为自己的
        requestBody.put("client_id", "2636917288");
        // 申请应用时分配的AppSecret
        // TODO: 等审核通过后需要修改为自己的
        requestBody.put("client_secret", "6a263e9284c6c1a74a62eadacc11b6e2");
        // 请求的类型
        requestBody.put("grant_type", "authorization_code");
        // 回调地址，需需与注册应用里的回调地址一致
        // TODO: 等审核通过后需要修改为自己的
        requestBody.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/weibo/success");
        // 调用authorize获得的code值
        requestBody.put("code", code);
        HttpResponse postResp = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", new HashMap<>(), new HashMap<>(), requestBody);

        // 2. 从返回结果中解析中access_token和uid，登陆注册这个用户
        if (postResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String jsonStr = EntityUtils.toString(postResp.getEntity());
            SocialUserTO socialUser = JSON.parseObject(jsonStr, SocialUserTO.class);
            // RPC调用会员服务去登陆&自动注册这个用户
            CommonResponse resp = CommonResponse.convertToResp(memberService.weiboLogin(socialUser));
            if (resp.getCode() == HttpStatus.SC_OK) {
                // 将登陆的用户信息存入session中
                MemberTO loginUser = resp.getData(new TypeReference<MemberTO>(){});
                session.setAttribute("loginUser", loginUser);
                return;
            } else {
                // RPC会员服务登陆接口失败
                throw new WeiboOAuth2RpcFail();
            }
        } else {
            // 微博访问权限获取失败
            throw new WeiboOAuth2AccessFail();
        }
    }
}
