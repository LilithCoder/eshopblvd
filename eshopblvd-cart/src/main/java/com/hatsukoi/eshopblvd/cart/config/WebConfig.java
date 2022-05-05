package com.hatsukoi.eshopblvd.cart.config;

import com.hatsukoi.eshopblvd.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web配置
 * @author gaoweilin
 * @date 2022/05/05 Thu 3:10 AM
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 给所有请求路径都加上了这个购物车的拦截器
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
    }
}
