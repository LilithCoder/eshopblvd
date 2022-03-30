package com.hatsukoi.eshopblvd.coupon;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDubbo
@EnableDiscoveryClient
@MapperScan("com.hatsukoi.eshopblvd.coupon.dao")
@SpringBootApplication(scanBasePackages = "com.hatsukoi.eshopblvd")
public class EshopblvdCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(EshopblvdCouponApplication.class, args);
    }

}
