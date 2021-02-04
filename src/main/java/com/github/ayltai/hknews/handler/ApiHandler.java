package com.github.ayltai.hknews.handler;

import com.amazonaws.cache.Cache;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.ayltai.hknews.Configuration;
import com.github.ayltai.hknews.util.ExpiringCache;

import org.jetbrains.annotations.NotNull;

public abstract class ApiHandler extends Handler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static Cache<String, Object> CACHE;

    @NotNull
    protected Cache<String, Object> getCache() {
        if (ApiHandler.CACHE == null) ApiHandler.CACHE = new ExpiringCache<>(Configuration.DEFAULT.getCacheExpirationMillis());

        return ApiHandler.CACHE;
    }
}
