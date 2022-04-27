package com.hatsukoi.eshopblvd.authserver.service;

import com.hatsukoi.eshopblvd.authserver.exception.SmsCodeNonmatchException;
import com.hatsukoi.eshopblvd.authserver.exception.SmsCodeTimeoutException;
import com.hatsukoi.eshopblvd.authserver.exception.SmsFrequentException;
import com.hatsukoi.eshopblvd.authserver.vo.UserRegisterVO;

/**
 * @author gaoweilin
 * @date 2022/04/28 Thu 3:50 AM
 */
public interface AuthService {
    void sendSmsCode(String phone) throws SmsFrequentException;

    void register(UserRegisterVO userRegisterVO) throws SmsCodeNonmatchException, SmsCodeTimeoutException;
}
