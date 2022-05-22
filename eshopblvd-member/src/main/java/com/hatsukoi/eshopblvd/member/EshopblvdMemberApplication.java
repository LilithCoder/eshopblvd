package com.hatsukoi.eshopblvd.member;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableDubbo
@EnableDiscoveryClient
@EnableRedisHttpSession
@MapperScan("com.hatsukoi.eshopblvd.member.dao")
@SpringBootApplication(scanBasePackages = "com.hatsukoi.eshopblvd")
public class EshopblvdMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(EshopblvdMemberApplication.class, args);
    }

}
