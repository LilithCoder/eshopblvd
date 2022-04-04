package com.hatsukoi.eshopblvd.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.hatsukoi.eshopblvd")
public class EshopblvdWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(EshopblvdWareApplication.class, args);
    }

}
