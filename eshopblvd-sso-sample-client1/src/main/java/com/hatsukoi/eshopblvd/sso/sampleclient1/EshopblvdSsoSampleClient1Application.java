package com.hatsukoi.eshopblvd.sso.sampleclient1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class EshopblvdSsoSampleClient1Application {

    public static void main(String[] args) {
        SpringApplication.run(EshopblvdSsoSampleClient1Application.class, args);
    }

}
