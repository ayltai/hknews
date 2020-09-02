package com.github.ayltai.hknews.task;

import java.util.Calendar;
import javax.transaction.Transactional;

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
    public PurgeTask(@NonNull final ItemRepository itemRepository, @NonNull final MainConfiguration configuration) {
        this.itemRepository = itemRepository;
        this.retentionDays  = configuration.getRetentionDays();
    }

    @Async
    @Scheduled(
        initialDelay = MainConfiguration.INITIAL_DELAY_PURGE,
        fixedDelay   = MainConfiguration.PERIOD_PURGE)
    @Transactional
    @Override
    public void run() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -retentionDays);

        this.itemRepository.deleteByPublishDateBefore(calendar.getTime());
    }
}
