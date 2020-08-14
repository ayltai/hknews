package com.github.ayltai.hknews;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import lombok.Getter;
import lombok.Setter;

@Primary
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "app")
public class MainConfiguration {
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String DEFAULT_PAGE_SIZE = "100";

    public static final int INITIAL_DELAY_INIT  = 30 * 1000;
    public static final int INITIAL_DELAY_PARSE = 60 * 1000;
    public static final int INITIAL_DELAY_PURGE = 2 * 60 * 1000;
    public static final int PERIOD_PARSE        = 2 * 10 * 60 * 1000;
    public static final int PERIOD_PURGE        = 60 * 60 * 1000;

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
