package com.github.ayltai.hknews.handler;

import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.github.ayltai.hknews.Configuration;
import com.github.ayltai.hknews.data.AmazonDynamoDBFactory;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.DefaultContentServiceFactory;
import com.github.ayltai.hknews.parser.DefaultParserFactory;
import com.github.ayltai.hknews.parser.Parser;
import com.github.ayltai.hknews.parser.ParserFactory;
import com.github.ayltai.hknews.service.ItemService;
import com.github.ayltai.hknews.service.SourceService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ParserHandler extends CronHandler {
    private SourceService sourceService;
    private ItemService   itemService;
    private ParserFactory parserFactory;
    private Parser        parser;

    @NotNull
    protected abstract String getSourceName();

    @NotNull
    private SourceService getSourceService(@NotNull final LambdaLogger logger) {
        try {
            if (this.sourceService == null) this.sourceService = new SourceService(new SourceRepository(AmazonDynamoDBFactory.create(), logger), logger);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }

        return this.sourceService;
    }

    @NotNull
    private ItemService getItemService(@NotNull final LambdaLogger logger) {
        try {
            if (this.itemService == null) this.itemService = new ItemService(new ItemRepository(AmazonDynamoDBFactory.create(), logger), logger);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }

        return this.itemService;
    }

    @NotNull
    private Parser getParser(@NotNull final LambdaLogger logger) {
        if (this.parser == null) this.parser = this.getParserFactory(logger).create(this.getSourceName(), logger);

        return this.parser;
    }

    @NotNull
    private ParserFactory getParserFactory(@NotNull final LambdaLogger logger) {
        if (this.parserFactory == null) this.parserFactory = new DefaultParserFactory(this.getSourceService(logger), new DefaultContentServiceFactory(Configuration.DEFAULT));

        return this.parserFactory;
    }

    @Nullable
    @Override
    public Void handleRequest(@NotNull final ScheduledEvent event, @NotNull final Context context) {
        final SourceService sourceService = this.getSourceService(context.getLogger());
        final ItemService   itemService   = this.getItemService(context.getLogger());
        final Parser        parser        = this.getParser(context.getLogger());

        sourceService.getSources(this.getSourceName())
            .forEach(source -> {
                try {
                    itemService.putItems(parser.getItems(source.getCategoryName())
                        .stream()
                        .filter(item -> itemService.getItem(item.toUid()).isEmpty())
                        .map(item -> {
                            try {
                                return parser.updateItem(item);
                            } catch (final Throwable e) {
                                this.log(e, event, context);
                            }

                            return item;
                        })
                        .collect(Collectors.toList()));
                } catch (final Throwable e) {
                    this.log(e, event, context);
                }
            });

        return null;
    }
}
