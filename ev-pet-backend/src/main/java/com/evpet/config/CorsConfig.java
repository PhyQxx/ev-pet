package com.evpet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    private static final String DEFAULT_ALLOWED_ORIGINS =
            "http://localhost:5177,http://localhost:8010,http://localhost:9081";

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        String origins = System.getenv().getOrDefault("CORS_ALLOWED_ORIGINS", DEFAULT_ALLOWED_ORIGINS);
        for (String origin : origins.split(",")) {
            config.addAllowedOriginPattern(origin.trim());
        }

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
