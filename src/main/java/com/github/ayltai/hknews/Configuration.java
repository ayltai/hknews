package com.github.ayltai.hknews;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Configuration {
    public static final Configuration DEFAULT = new Configuration(4, 30, 60, 60, 4, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:85.0) Gecko/20100101 Firefox/85.0", 7);

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
