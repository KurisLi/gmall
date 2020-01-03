package com.atguigu.gmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author lzzzzz
 * @create 2020-01-03 16:28
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter getCorsWebFilter(){
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        //允许访问的域，不要写*，否则cookie就无法使用了
        config.addAllowedOrigin("http://127.0.0.1:1000");
        config.addAllowedOrigin("http://localhost:1000");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        configSource.registerCorsConfiguration("/**",config);
        CorsWebFilter corsWebFilter = new CorsWebFilter(configSource);
        return corsWebFilter;
    }
}
