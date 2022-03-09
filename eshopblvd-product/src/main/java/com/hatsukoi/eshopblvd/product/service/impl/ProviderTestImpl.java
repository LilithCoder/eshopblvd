package com.hatsukoi.eshopblvd.product.service.impl;

import org.springframework.stereotype.Service;

/**
 * @author gaoweilin
 * @date 2022/03/09 Wed 2:46 AM
 */
@com.alibaba.dubbo.config.annotation.Service
@Service
public class ProviderTestImpl implements com.hatsukoi.eshopblvd.product.service.ProviderTest {

    @Override
    public String getProviderResponse() {
        return "You get response from provider!";
    }
}
