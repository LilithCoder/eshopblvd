package com.hatsukoi.eshopblvd.consumer;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.hatsukoi.eshopblvd")
public class DubboConsumerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboConsumerDemoApplication.class, args);
    }

}
