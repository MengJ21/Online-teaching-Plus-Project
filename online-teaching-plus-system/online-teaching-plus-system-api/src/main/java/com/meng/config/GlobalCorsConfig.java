package com.meng.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author 梦举
 * @version 1.0
 * @description 跨域拦截器
 * @date 2023/3/16 15:52
 */

@Configuration
public class GlobalCorsConfig {

    /**
    * @description 允许跨域调用的拦截器
    *
    * @return org.apache.catalina.filters.CorsFilter
    * @author 梦举
    * @date 2023/3/16 15:56
    */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许白名单域名进行跨域调用
        configuration.addAllowedOrigin("*");
        // 允许跨域发送cookie。
        configuration.setAllowCredentials(true);
        // 放行全部原始头信息。
        configuration.addAllowedHeader("*");
        // 允许所有请求方法跨域调用
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }
}