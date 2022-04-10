package com.hatsukoi.eshopblvd.ware.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis配置类
 * @author gaoweilin
 * @date 2022/04/04 Mon 11:43 PM
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.hatsukoi.eshopblvd.ware.dao")
public class WareMyBatisConfig {

}
