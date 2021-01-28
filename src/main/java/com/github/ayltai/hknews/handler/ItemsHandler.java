package com.github.ayltai.hknews.handler;

import java.util.Arrays;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.ayltai.hknews.data.AmazonDynamoDBFactory;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.service.ItemService;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

public final class ItemsHandler extends ApiHandler {
    @NotNull
    @Override
    public APIGatewayProxyResponseEvent handleRequest(@NotNull final APIGatewayProxyRequestEvent event, @NotNull final Context context) {
        final String sourceNames   = event.getPathParameters().get("sourceNames");
        final String categoryNames = event.getPathParameters().get("categoryNames");
        final String days          = event.getPathParameters().get("days");

        if (sourceNames == null || categoryNames == null || days == null) return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.SC_BAD_REQUEST);

        try {
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatus.SC_OK)
                .withBody(Handler.GSON.toJson(new ItemService(new ItemRepository(AmazonDynamoDBFactory.create())).getItems(Arrays.asList(sourceNames.split(",")), Arrays.asList(categoryNames.split(",")), Integer.parseInt(days))));
        } catch (final InterruptedException e) {
            this.log(e, event, context);

            return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
