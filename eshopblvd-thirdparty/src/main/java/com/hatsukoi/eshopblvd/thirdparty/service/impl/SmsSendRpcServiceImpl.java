package com.hatsukoi.eshopblvd.thirdparty.service.impl;

import com.hatsukoi.eshopblvd.api.thirdparty.SmsSendRpcService;
import com.hatsukoi.eshopblvd.thirdparty.component.SmsComponent;
import com.hatsukoi.eshopblvd.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/04/26 Tue 2:42 AM
 */
@Service
@org.apache.dubbo.config.annotation.Service
public class SmsSendRpcServiceImpl implements SmsSendRpcService {
    @Autowired
    private SmsComponent smsComponent;

    /**
     * 调用三方服务来发送短信验证码
     * @param phone
     * @param code
     * @return
     */
    @Override
    public CommonResponse sendCode(String phone, String code) {
        smsComponent.sendSmsCode(phone, code);
        return CommonResponse.success();
    }
}
