package com.hatsukoi.eshopblvd.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 * @author gaoweilin
 * @date 2022/04/25 Mon 1:57 AM
 */
@Configuration
public class ThreadPoolExecutorConfig {

    /**
     * 为容器注入一个线程池
     * @param configProperties
     * @return
     */
    @Bean
    public ThreadPoolExecutor getThreadPoolExecutor(ThreadPoolConfigProperties configProperties) {
        return new ThreadPoolExecutor(configProperties.getCoreSize(),
                configProperties.getMaxSize(),
                configProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10000),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
