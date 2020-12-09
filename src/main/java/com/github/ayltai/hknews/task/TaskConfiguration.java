package com.github.ayltai.hknews.task;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling
@Profile("!test")
public class TaskConfiguration {
    static final long INITIAL_DELAY = 30 * 1000;
    static final long PARSE_DELAY   = 60 * 1000;
    static final long PARSE_PERIOD  = 20 * 60 * 1000;
    static final long PURGE_DELAY   = 15 * 60 * 1000;
    static final long PURGE_PERIOD  = 6 * 60 * 60 * 1000;
}
