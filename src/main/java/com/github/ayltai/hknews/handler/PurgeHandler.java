package com.github.ayltai.hknews.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.github.ayltai.hknews.Configuration;
import com.github.ayltai.hknews.data.AmazonDynamoDBFactory;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.service.ItemService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PurgeHandler extends CronHandler {
    private ItemService itemService;

    @NotNull
    private ItemService getItemService(@NotNull final LambdaLogger logger) {
        try {
            if (this.itemService == null) this.itemService = new ItemService(new ItemRepository(AmazonDynamoDBFactory.create(), logger), logger);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }

        return this.itemService;
    }

    @Nullable
    @Override
    public Void handleRequest(@NotNull final ScheduledEvent event, @NotNull final Context context) {
        this.getItemService(context.getLogger()).removeOldItems(Configuration.DEFAULT.getRetentionDays());

        return null;
    }
}
