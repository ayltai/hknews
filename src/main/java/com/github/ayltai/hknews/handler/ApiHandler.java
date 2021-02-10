package com.github.ayltai.hknews.handler;

import java.net.URISyntaxException;
import java.util.stream.Collectors;

import com.amazonaws.cache.Cache;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.ayltai.hknews.Configuration;
import com.github.ayltai.hknews.util.ExpiringCache;

import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ApiHandler extends Handler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Cache<String, Object> CACHE = new ExpiringCache<>(Configuration.DEFAULT.getCacheExpirationMillis());

    @Nullable
    protected Object getCache(@NotNull final APIGatewayProxyRequestEvent event, @NotNull final Context context) {
        return ApiHandler.CACHE.get(this.getCacheKey(event, context));
    }

    protected void putCache(@NotNull final APIGatewayProxyRequestEvent event, @NotNull final Context context, @Nullable final Object value) {
        ApiHandler.CACHE.put(this.getCacheKey(event, context), value);
    }

    @NotNull
    private String getCacheKey(@NotNull final APIGatewayProxyRequestEvent event, @NotNull final Context context) {
        final URIBuilder builder = new URIBuilder().setPath(event.getPath());
        try {
            return event.getQueryStringParameters() == null ? builder.build().toASCIIString() : builder.setParameters(event.getQueryStringParameters().entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue())).collect(Collectors.toList())).build().toASCIIString();
        } catch (final URISyntaxException e) {
            this.log(e, event, context);
        }

        return event.getPath();
    }
}
