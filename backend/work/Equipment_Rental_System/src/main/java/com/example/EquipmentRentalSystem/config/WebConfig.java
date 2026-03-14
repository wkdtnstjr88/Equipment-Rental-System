package com.example.EquipmentRentalSystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/", "/members/add", "/login", "/logout",
                        "/equipments/**",           // 🔥 장비 현황 페이지 제외
                        "/rentals/history/**",      // 🔥 대여 이력 페이지 제외
                        "/css/**", "/js/**", "/img/**", "/*.ico", "/error"
                );
    }
}