package com.hatsukoi.eshopblvd.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 线程池参数配置（via配置文件）
 * @author gaoweilin
 * @date 2022/04/25 Mon 1:59 AM
 */
@Data
@Component
@ConfigurationProperties(prefix = "eshopblvd.thread-pool")
public class ThreadPoolConfigProperties {
    /**
     * 核心线程数
     */
    private Integer coreSize;
    /**
     * 最大线程数
     */
    private Integer maxSize;
    /**
     * 超出核心线程时最大存活时间
     */
    private Integer keepAliveTime;
}
