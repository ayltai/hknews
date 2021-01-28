package com.github.ayltai.hknews.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.ayltai.hknews.data.AmazonDynamoDBFactory;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.service.ItemService;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

public final class ItemHandler extends ApiHandler {
    @NotNull
    @Override
    public APIGatewayProxyResponseEvent handleRequest(@NotNull final APIGatewayProxyRequestEvent event, @NotNull final Context context) {
        final String uid = event.getPathParameters().get("uid");
        if (uid == null) return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.SC_BAD_REQUEST);

        try {
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatus.SC_OK)
                .withBody(Handler.GSON.toJson(new ItemService(new ItemRepository(AmazonDynamoDBFactory.create())).getItem(uid)));
        } catch (final InterruptedException e) {
            this.log(e, event, context);

            return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
