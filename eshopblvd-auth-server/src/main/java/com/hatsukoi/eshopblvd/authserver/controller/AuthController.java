package com.hatsukoi.eshopblvd.authserver.controller;

import com.hatsukoi.eshopblvd.authserver.exception.*;
import com.hatsukoi.eshopblvd.authserver.service.AuthService;
import com.hatsukoi.eshopblvd.authserver.vo.UserLoginVO;
import com.hatsukoi.eshopblvd.authserver.vo.UserRegisterVO;
import com.hatsukoi.eshopblvd.exception.BizCodeEnum;
import com.hatsukoi.eshopblvd.to.MemberTO;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 认证控制器：（注册&登陆&短信验证码）
 * @author gaoweilin
 * @date 2022/04/26 Tue 2:52 AM
 */
@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * 发送短信验证码接口
     * @param phone
     * @return
     */
    @GetMapping("/sms/sendcode")
    public CommonResponse sendSmsCode(@RequestParam("phone") String phone) {
        try {
            authService.sendSmsCode(phone);
        } catch (SmsFrequentException exception) {
            return CommonResponse.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
        }
        return CommonResponse.success();
    }

    /**
     * 新用户注册接口
     * @param userRegisterVO
     * @param result
     * @return
     */
    @PostMapping("/register")
    public CommonResponse register(@Valid UserRegisterVO userRegisterVO, BindingResult result) {
        Map<String, String> errPrompt;

        // 1. 如果提交的注册信息校验出错，直接返回出错信息
        if (result.hasErrors()) {
            errPrompt = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            // 返回这些数据校验错误的信息，在注册页上展示
            return CommonResponse.error().setData(errPrompt);
        }

        // 2. 开始注册业务逻辑，根据抛出的不同异常来返回不同的错误信息提示
        try {
            authService.register(userRegisterVO);
        } catch (SmsCodeNonmatchException exception) {
            // 验证码匹配错误
            errPrompt = new HashMap<>();
            errPrompt.put("verification_code", BizCodeEnum.SMS_CODE_NONMATCH_EXCEPTION.getMsg());
            // 返回这些数据校验错误的信息，在注册页上展示
            return CommonResponse.error(BizCodeEnum.SMS_CODE_NONMATCH_EXCEPTION.getCode()).setData(errPrompt);
        } catch (SmsCodeTimeoutException exception) {
            // 验证码10min过期或者用户根本没有获取验证码
            errPrompt = new HashMap<>();
            errPrompt.put("verification_code", BizCodeEnum.SMS_CODE_TIMEOUT_EXCEPTION.getMsg());
            // 返回这些数据校验错误的信息，在注册页上展示
            return CommonResponse.error(BizCodeEnum.SMS_CODE_TIMEOUT_EXCEPTION.getCode()).setData(errPrompt);
        } catch (PhoneExistException exception) {
            errPrompt = new HashMap<>();
            errPrompt.put("phone", BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
            // 返回这些数据校验错误的信息，在注册页上展示
            return CommonResponse.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode()).setData(errPrompt);
        } catch (UserExistException exception) {
            errPrompt = new HashMap<>();
            errPrompt.put("userName", BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
            // 返回这些数据校验错误的信息，在注册页上展示
            return CommonResponse.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode()).setData(errPrompt);
        }
        // 3. 注册成功，返回后页面跳转到登陆页
        return CommonResponse.success();
    }

    /**
     * 用户登陆接口
     * @param userLoginVO
     * @param session
     * @return
     */
    @PostMapping("/login")
    public CommonResponse login(UserLoginVO userLoginVO, HttpSession session) {
        try {
            // 调用登陆业务
            authService.login(userLoginVO, session);
        } catch (LoginAcctNonExistException exception) {
            // 返回错误信息，在登陆页上展示
            return CommonResponse.error(BizCodeEnum.LOGINACCT_NONEXIST_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_NONEXIST_EXCEPTION.getMsg());
        } catch (LoginAcctPasswordInvalidException exception) {
            // 返回错误信息，在登陆页上展示
            return CommonResponse.error(BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
        }
        // 登陆成功，返回首页eshopblvd.com
        return CommonResponse.success();
    }
}




























