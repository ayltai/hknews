package com.github.ayltai.hknews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@SpringBootApplication
@EnableConfigurationProperties(MainConfiguration.class)
public class MainApplication {
    public static void main(@Nullable final String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    @NonNull
    protected HttpHeaders getDefaultHttpHeaders() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Content-Type-Options", "nosniff");
        httpHeaders.set("X-Frame-Options", "DENY");
        httpHeaders.set("X-XSS-Protection", "1; mode=block");

        return httpHeaders;
    }
}
