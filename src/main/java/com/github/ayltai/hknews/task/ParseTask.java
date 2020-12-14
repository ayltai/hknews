package com.github.ayltai.hknews.task;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.ayltai.hknews.parser.Parser;
import com.github.ayltai.hknews.parser.ParserFactory;
import com.github.ayltai.hknews.service.ItemService;
import com.github.ayltai.hknews.service.SourceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ParseTask implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParseTask.class);

    private final SourceService sourceService;
    private final ItemService   itemService;
    private final ParserFactory parserFactory;

    @Autowired
    public ParseTask(@NonNull final SourceService sourceService, @NonNull final ItemService itemService, @NonNull final ParserFactory parserFactory) {
        this.sourceService = sourceService;
        this.itemService   = itemService;
        this.parserFactory = parserFactory;
    }

    @Async
    @Scheduled(
        initialDelay = TaskConfiguration.PARSE_DELAY,
        fixedRate    = TaskConfiguration.PARSE_PERIOD
    )
    @Override
    public void run() {
        this.sourceService
            .getSources()
            .parallelStream()
            .forEach(source -> {
                final Parser parser = this.parserFactory.create(source.getName());

                try {
                    this.itemService
                        .putItems(parser.getItems(source.getCategoryName())
                        .parallelStream()
                        .filter(item -> this.itemService.getItem(item.getUrl()).isEmpty())
                        .map(item -> {
                            try {
                                return parser.updateItem(item);
                            } catch (final Throwable e) {
                                ParseTask.LOGGER.error(String.format("Failed to parse %s", item.getUrl()), e);
                            }

                            return item;
                        })
                        .collect(Collectors.toList()));
                } catch (final Throwable e) {
                    ParseTask.LOGGER.error(String.format("Failed to parse %1$s (%2$s)", source.getName(), source.getCategoryName()), e);
                }
            });
    }
}
