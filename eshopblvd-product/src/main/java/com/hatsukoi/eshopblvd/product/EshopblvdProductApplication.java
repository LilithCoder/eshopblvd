package com.hatsukoi.eshopblvd.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.hatsukoi.eshopblvd.product.dao")
@SpringBootApplication(scanBasePackages = "com.hatsukoi.eshopblvd")
public class EshopblvdProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(EshopblvdProductApplication.class, args);
    }

}
