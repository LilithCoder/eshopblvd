package com.hatsukoi.eshopblvd.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableDiscoveryClient
@MapperScan("com.hatsukoi.eshopblvd.ware.dao")
@SpringBootApplication(scanBasePackages = "com.hatsukoi.eshopblvd")
public class EshopblvdWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(EshopblvdWareApplication.class, args);
    }

}
