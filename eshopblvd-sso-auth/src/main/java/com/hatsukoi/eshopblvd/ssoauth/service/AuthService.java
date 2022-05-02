package com.hatsukoi.eshopblvd.ssoauth.service;

import com.hatsukoi.eshopblvd.ssoauth.exception.*;
import com.hatsukoi.eshopblvd.ssoauth.vo.UserLoginVO;
import com.hatsukoi.eshopblvd.ssoauth.vo.UserRegisterVO;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author gaoweilin
 * @date 2022/04/28 Thu 3:50 AM
 */
public interface AuthService {
    void sendSmsCode(String phone) throws SmsFrequentException;

    void register(UserRegisterVO userRegisterVO) throws SmsCodeNonmatchException, SmsCodeTimeoutException;

    void login(UserLoginVO userLoginVO, HttpSession session) throws LoginAcctNonExistException, LoginAcctPasswordInvalidException;

    void weiboLogin(String code, HttpSession session, HttpServletResponse response) throws Exception;
}
