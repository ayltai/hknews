package com.github.ayltai.hknews;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Configuration {
    public static final Configuration DEFAULT = new Configuration(512, 30, 30, 4, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:86.0) Gecko/20100101 Firefox/86.0", 3, 1, 10, "Hongkong", 1_200_000L);

    @Getter
    @Setter
    private int connectionPoolSize;

    @Getter
    @Setter
    private int socketTimeout;

    @Getter
    @Setter
    private int connectTimeout;

    @Getter
    @Setter
    private int idleTimeout;

    @Getter
    @Setter
    private String userAgent;

    @Getter
    @Setter
    private int retentionDays;

    @Getter
    @Setter
    private int pageNumber;

    @Getter
    @Setter
    private int pageSize;

    @Getter
    @Setter
    private String timeZone;

    @Getter
    @Setter
    private long cacheExpirationMillis;
}
