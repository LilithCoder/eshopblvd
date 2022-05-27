package com.hatsukoi.eshopblvd.seckill;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class EshopblvdSecKillApplication {

    public static void main(String[] args) {
        SpringApplication.run(EshopblvdSecKillApplication.class, args);
    }

}
