package com.hatsukoi.eshopblvd.api.thirdparty;

import com.hatsukoi.eshopblvd.utils.CommonResponse;

import java.util.Map;

/**
 * @author gaoweilin
 * @date 2022/04/26 Tue 2:47 AM
 */
public interface SmsSendRpcService {
    public Map<String, Object> sendCode(String phone, String code);
}
