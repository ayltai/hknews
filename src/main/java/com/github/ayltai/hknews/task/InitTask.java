package com.github.ayltai.hknews.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.ayltai.hknews.MainConfiguration;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.service.SourceService;

@Component
public class InitTask implements Runnable {
    private final SourceRepository sourceRepository;

    @Autowired
    public InitTask(@NonNull final SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @Scheduled(
        initialDelay = MainConfiguration.INITIAL_DELAY_INIT,
        fixedDelay   = Long.MAX_VALUE)
    @Override
    public void run() {
        new SourceService(this.sourceRepository).getSourceNames();
    }
}
