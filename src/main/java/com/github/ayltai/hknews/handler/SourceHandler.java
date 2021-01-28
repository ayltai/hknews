package com.github.ayltai.hknews.handler;

import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.ayltai.hknews.data.AmazonDynamoDBFactory;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.service.SourceService;

import org.jetbrains.annotations.NotNull;

public final class SourceHandler extends ApiHandler {
    @NotNull
    @Override
    public APIGatewayProxyResponseEvent handleRequest(@NotNull final APIGatewayProxyRequestEvent event, @NotNull final Context context) {
        try {
            return APIGatewayProxyResponseEventFactory.ok(new SourceService(new SourceRepository(AmazonDynamoDBFactory.create(), context.getLogger()), context.getLogger()).getSources().stream().map(Source::getSourceName).distinct().collect(Collectors.toList()));
        } catch (final InterruptedException e) {
            this.log(e, event, context);
        }

        return APIGatewayProxyResponseEventFactory.badRequest();
    }
}
