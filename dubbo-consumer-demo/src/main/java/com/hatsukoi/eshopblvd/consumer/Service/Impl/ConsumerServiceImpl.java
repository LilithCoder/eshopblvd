package com.hatsukoi.eshopblvd.consumer.Service.Impl;

import com.hatsukoi.eshopblvd.consumer.Service.ConsumerService;
import com.hatsukoi.eshopblvd.provider.Service.ProviderService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

/**
 * @author gaoweilin
 * @date 2022/03/10 Thu 1:42 AM
 */
@Service
@org.apache.dubbo.config.annotation.Service
public class ConsumerServiceImpl implements ConsumerService {
    @Reference(interfaceName = "com.hatsukoi.eshopblvd.provider.Service.ProviderService")
    ProviderService providerService;

    @Override
    public String requestProvider() {
        return providerService.getProviderResponse();
    }
}
