package com.hatsukoi.eshopblvd.provider;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.hatsukoi.eshopblvd")
public class DubboProviderDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboProviderDemoApplication.class, args);
    }

}
