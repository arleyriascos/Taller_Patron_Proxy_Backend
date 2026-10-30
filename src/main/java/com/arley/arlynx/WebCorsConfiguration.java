package com.arley.arlynx;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebCorsConfiguration implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public WebCorsConfiguration(@Value("${arlynx.cors.allowed-origins}") String[] allowedOrigins) {
        this.allowedOrigins = Arrays.stream(allowedOrigins)
                .map(origin -> origin.strip().replaceAll("/+$", ""))
                .toArray(String[]::new);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Retry-After");
    }
}
