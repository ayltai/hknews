package com.github.ayltai.hknews;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.github.ayltai.hknews.parser.Parser;
import com.github.ayltai.hknews.parser.ParserFactory;
import com.github.ayltai.hknews.service.ItemService;
import com.github.ayltai.hknews.service.SourceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Profile("serverless")
@Component
public final class ServerlessRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerlessRunner.class);

    private final SourceService sourceService;
    private final ItemService   itemService;
    private final ParserFactory parserFactory;

    @Autowired
    public ServerlessRunner(@NonNull final SourceService sourceService, @NonNull final ItemService itemService, @NonNull final ParserFactory parserFactory) {
        this.sourceService = sourceService;
        this.itemService   = itemService;
        this.parserFactory = parserFactory;
    }

    @Override
    public void run(final String... args) throws Exception {
        this.sourceService
            .getSources(args[args.length - 1])
            .parallelStream()
            .forEach(source -> {
                final Parser parser = this.parserFactory.create(source.getName());

                try {
                    this.itemService.putItems(parser.getItems(source.getCategoryName())
                        .parallelStream()
                        .map(item -> {
                            try {
                                return parser.updateItem(item);
                            } catch (final Throwable e) {
                                ServerlessRunner.LOGGER.error(String.format("Failed to parse %s", item.getUrl()), e);
                            }

                            return item;
                        })
                        .collect(Collectors.toList()));
                } catch (final Throwable e) {
                    ServerlessRunner.LOGGER.error(String.format("Failed to parse %1$s (%2$s)", source.getName(), source.getCategoryName()), e);
                }
            });
    }
}
