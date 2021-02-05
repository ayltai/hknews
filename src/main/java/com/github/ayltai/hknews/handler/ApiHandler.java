package com.github.ayltai.hknews.handler;

import com.amazonaws.cache.Cache;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.ayltai.hknews.Configuration;
import com.github.ayltai.hknews.util.ExpiringCache;

public abstract class ApiHandler extends Handler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    protected static final Cache<String, Object> CACHE = new ExpiringCache<>(Configuration.DEFAULT.getCacheExpirationMillis());
}
