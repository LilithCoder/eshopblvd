package com.hatsukoi.eshopblvd.order.service.impl;

import com.hatsukoi.eshopblvd.order.service.ConsumerTestService;
import com.hatsukoi.eshopblvd.product.service.ProviderTest;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

/**
 * @author gaoweilin
 * @date 2022/03/09 Wed 2:32 AM
 */

@Service
@org.apache.dubbo.config.annotation.Service
public class ConsumerTestServiceImpl implements ConsumerTestService {
    @Reference(check = false)
    ProviderTest providerTest;

    @Override
    public String consumerTest() {
        return providerTest.getProviderResponse();
    }

}
