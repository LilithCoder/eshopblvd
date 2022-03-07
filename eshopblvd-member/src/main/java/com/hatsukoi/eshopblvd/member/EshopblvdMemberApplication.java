package com.hatsukoi.eshopblvd.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@MapperScan("com.hatsukoi.eshopblvd.member.dao")
@SpringBootApplication(scanBasePackages = "com.hatsukoi.eshopblvd")
public class EshopblvdMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(EshopblvdMemberApplication.class, args);
    }

}
