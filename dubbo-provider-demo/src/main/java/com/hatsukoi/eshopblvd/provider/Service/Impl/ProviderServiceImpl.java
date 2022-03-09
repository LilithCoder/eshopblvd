package com.hatsukoi.eshopblvd.provider.Service.Impl;

import com.hatsukoi.eshopblvd.provider.Service.ProviderService;
import org.springframework.stereotype.Service;

/**
 * @author gaoweilin
 * @date 2022/03/10 Thu 1:44 AM
 */
@Service
@org.apache.dubbo.config.annotation.Service
public class ProviderServiceImpl implements ProviderService {
    @Override
    public String getProviderResponse() {
        return "You get response from provider!";
    }
}
