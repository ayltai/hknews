package com.github.ayltai.hknews.task;

import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.util.stream.Collectors;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Async
    @Scheduled(
        initialDelay = MainConfiguration.INITIAL_DELAY_PARSE,
        fixedRate    = MainConfiguration.PERIOD_PARSE)
    @Override
    public void run() {
        this.sourceService
            .getSourceNamesAndCategoryNames()
            .forEach(pair -> {
                final Parser parser = this.parserFactory.create(pair.getFirst());

                try {
                    this.itemService.saveItems(parser.getItems(pair.getSecond())
                        .stream()
                        .map(item -> {
                            try {
                                return parser.updateItem(item);
                            } catch (final ProtocolException e) {
                                if (e.getMessage().startsWith("Too many follow-up requests")) ParseTask.LOGGER.info(e.getMessage(), e);
                            } catch (final SSLHandshakeException | SocketTimeoutException e) {
                                ParseTask.LOGGER.info(e.getMessage(), e);
                            } catch (final SSLException e) {
                                if (e.getMessage().equals("Connection reset")) ParseTask.LOGGER.info(e.getMessage(), e);
                            } catch (final Throwable e) {
                                ParseTask.LOGGER.error(String.format("Failed to parse news details for %1$s (%2$s)", parser.getSourceName(), item.getUrl()));
                                ParseTask.LOGGER.error(e.getMessage(), e);
                            }

                            return item;
                        })
                        .collect(Collectors.toList()));
                } catch (final Throwable e) {
                    ParseTask.LOGGER.error(String.format("Failed to parse news list for %1$s (%2$s)", parser.getSourceName(), pair.getFirst()));
                    ParseTask.LOGGER.error(e.getMessage(), e);
                }
            });
    }
}
