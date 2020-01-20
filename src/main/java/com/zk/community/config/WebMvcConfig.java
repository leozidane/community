package com.zk.community.config;

import com.zk.community.controller.interceptor.LoginRequiredInterceptor;
import com.zk.community.controller.interceptor.LoginTicketInterceptor;
import com.zk.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器的配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    MessageInterceptor messageInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor).excludePathPatterns(
                "/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.jpeg"
        );

        registry.addInterceptor(loginRequiredInterceptor).excludePathPatterns(
                "/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.jpeg"
        );
        registry.addInterceptor(messageInterceptor).excludePathPatterns(
                "/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.jpeg"
        );
    }
}
