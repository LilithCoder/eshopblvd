package com.hatsukoi.eshopblvd.order;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRabbit
@EnableDubbo
@EnableDiscoveryClient
@EnableRedisHttpSession
@MapperScan("com.hatsukoi.eshopblvd.order.dao")
@SpringBootApplication(scanBasePackages = "com.hatsukoi.eshopblvd")
@EnableAspectJAutoProxy(exposeProxy = true)
public class EshopblvdOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(EshopblvdOrderApplication.class, args);
    }

}
