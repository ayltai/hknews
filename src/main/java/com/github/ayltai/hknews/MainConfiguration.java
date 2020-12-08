package com.github.ayltai.hknews;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.Getter;
import lombok.Setter;

@Primary
@Configuration
@ConfigurationProperties(prefix = "app")
public class MainConfiguration {
    @Getter
    @Setter
    private int connectionPoolSize;

    @Getter
    @Setter
    private int connectTimeout;

    @Getter
    @Setter
    private int readTimeout;

    @Getter
    @Setter
    private int writeTimeout;

    @Getter
    @Setter
    private int idleTimeout;

    @Getter
    @Setter
    private String userAgent;

    @Getter
    @Setter
    private int retentionDays;
}
