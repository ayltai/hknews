package com.github.ayltai.hknews.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.ayltai.hknews.data.AmazonDynamoDBFactory;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.service.SourceService;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

public final class SourceHandler extends ApiHandler {
    @NotNull
    @Override
    public APIGatewayProxyResponseEvent handleRequest(@NotNull final APIGatewayProxyRequestEvent event, @NotNull final Context context) {
        try {
            return new APIGatewayProxyResponseEvent()
                .withStatusCode(HttpStatus.SC_OK)
                .withBody(Handler.GSON.toJson(new SourceService(new SourceRepository(AmazonDynamoDBFactory.create())).getSources()));
        } catch (final InterruptedException e) {
            this.log(e, event, context);

            return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
