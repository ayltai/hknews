package com.github.ayltai.hknews.handler;

import java.util.Optional;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.ayltai.hknews.data.AmazonDynamoDBFactory;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.service.ItemService;

import org.jetbrains.annotations.NotNull;

public final class ItemHandler extends ApiHandler {
    @NotNull
    @Override
    public APIGatewayProxyResponseEvent handleRequest(@NotNull final APIGatewayProxyRequestEvent event, @NotNull final Context context) {
        final String uid = event.getPathParameters().get("uid");
        if (uid == null) return APIGatewayProxyResponseEventFactory.badRequest();

        try {
            final Optional<Item> item = new ItemService(new ItemRepository(AmazonDynamoDBFactory.create(), context.getLogger()), context.getLogger()).getItem(uid);

            return item.isEmpty() ? APIGatewayProxyResponseEventFactory.notFound() : APIGatewayProxyResponseEventFactory.ok(item.get());
        } catch (final InterruptedException e) {
            this.log(e, event, context);
        }

        return APIGatewayProxyResponseEventFactory.error();
    }
}
