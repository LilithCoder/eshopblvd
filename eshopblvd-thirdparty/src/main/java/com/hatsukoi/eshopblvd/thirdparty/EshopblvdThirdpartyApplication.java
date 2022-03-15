package com.hatsukoi.eshopblvd.thirdparty;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.hatsukoi.eshopblvd")
public class EshopblvdThirdpartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(EshopblvdThirdpartyApplication.class, args);
    }

}
