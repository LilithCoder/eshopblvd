package com.hatsukoi.eshopblvd.gateway;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.hatsukoi.eshopblvd")
public class EshopblvdGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(EshopblvdGatewayApplication.class, args);
    }

}
