package com.hatsukoi.eshopblvd.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.hatsukoi.eshopblvd")
public class EshopblvdGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(EshopblvdGatewayApplication.class, args);
    }

}
