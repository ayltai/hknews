package com.github.ayltai.hknews.task;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.ayltai.hknews.MainConfiguration;
import com.github.ayltai.hknews.data.repository.ItemRepository;

@Component
public class PurgeTask implements Runnable {
    private final ItemRepository itemRepository;
    private final int            retentionDays;

    @Autowired
    public PurgeTask(@NonNull final ItemRepository itemRepository, @NonNull final MainConfiguration mainConfiguration) {
        this.itemRepository = itemRepository;
        this.retentionDays  = mainConfiguration.getRetentionDays();
    }

    @Async
    @Scheduled(
        initialDelay = TaskConfiguration.PURGE_DELAY,
        fixedDelay   = TaskConfiguration.PURGE_PERIOD
    )
    @Override
    public void run() {
        this.itemRepository.deleteAllByPublishDateBefore(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).minusDays(this.retentionDays).toInstant()));
    }
}
