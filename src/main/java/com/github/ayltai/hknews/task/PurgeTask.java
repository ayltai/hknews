package com.github.ayltai.hknews.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.ayltai.hknews.MainConfiguration;
import com.github.ayltai.hknews.service.ItemService;

@Component
public class PurgeTask implements Runnable {
    private final ItemService itemService;
    private final int         retentionDays;

    @Autowired
    public PurgeTask(@NonNull final ItemService itemService, @NonNull final MainConfiguration mainConfiguration) {
        this.itemService   = itemService;
        this.retentionDays = mainConfiguration.getRetentionDays();
    }

    @Async
    @Scheduled(
        initialDelay = TaskConfiguration.PURGE_DELAY,
        fixedDelay   = TaskConfiguration.PURGE_PERIOD
    )
    @Override
    public void run() {
        this.itemService.removeItems(this.retentionDays);
    }
}
