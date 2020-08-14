package com.github.ayltai.hknews.task;

import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.ayltai.hknews.MainConfiguration;
import com.github.ayltai.hknews.parser.Parser;
import com.github.ayltai.hknews.parser.ParserFactory;
import com.github.ayltai.hknews.service.ItemService;
import com.github.ayltai.hknews.service.SourceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ParseTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParseTask.class);

    private final ParserFactory parserFactory;
    private final SourceService sourceService;
    private final ItemService   itemService;

    @Autowired
    public ParseTask(@NonNull final ParserFactory parserFactory, @NonNull final SourceService sourceService, @NonNull final ItemService itemService) {
        this.parserFactory = parserFactory;
        this.sourceService = sourceService;
        this.itemService   = itemService;
    }

    @CacheEvict(
        cacheNames = "items",
        allEntries = true
    )
    @Async
    @Scheduled(
        initialDelay = MainConfiguration.INITIAL_DELAY_PARSE,
        fixedRate    = MainConfiguration.PERIOD_PARSE
    )
    @Override
    public void run() {
        this.sourceService
            .getSources(0, Integer.MAX_VALUE)
            .forEach(source -> source.getCategories()
                .forEach(category -> {
                    final Parser parser = this.parserFactory.create(source.getName());

                    try {
                        this.itemService.saveItems(parser.getItems(category.getName())
                            .stream()
                            .map(item -> {
                                try {
                                    return parser.updateItem(item);
                                } catch (final Throwable e) {
                                    ParseTask.LOGGER.error(String.format("Failed to parse news details for %s", parser.getSourceName()));
                                    ParseTask.LOGGER.error(e.getMessage(), e);
                                }

                                return item;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList()));
                    } catch (final Throwable e) {
                        ParseTask.LOGGER.error(String.format("Failed to parse news list for %s", parser.getSourceName()));
                        ParseTask.LOGGER.error(e.getMessage(), e);
                    }
                }));
    }
}
