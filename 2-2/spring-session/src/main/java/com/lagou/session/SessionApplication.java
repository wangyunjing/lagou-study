package com.lagou.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author yunjing.wang
 * @date 2020/10/19
 */
@SpringBootApplication
@EnableRedisHttpSession
public class SessionApplication implements WebMvcConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(SessionApplication.class, args);
    }

    @Autowired
    LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/login", "/favicon.ico");
    }
}
