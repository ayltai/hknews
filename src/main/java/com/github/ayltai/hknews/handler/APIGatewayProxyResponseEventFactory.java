package com.github.ayltai.hknews.handler;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import lombok.experimental.UtilityClass;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class APIGatewayProxyResponseEventFactory {
    @NotNull
    public APIGatewayProxyResponseEvent ok() {
        return APIGatewayProxyResponseEventFactory.ok(null);
    }

    @NotNull
    public APIGatewayProxyResponseEvent ok(@Nullable final Object body) {
        final APIGatewayProxyResponseEvent event = APIGatewayProxyResponseEventFactory.decorate(new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.SC_OK));
        return body == null ? event : event.withBody(new Gson().toJson(body));
    }

    @NotNull
    public APIGatewayProxyResponseEvent error() {
        return APIGatewayProxyResponseEventFactory.decorate(new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR));
    }

    @NotNull
    public APIGatewayProxyResponseEvent badRequest() {
        return APIGatewayProxyResponseEventFactory.decorate(new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.SC_BAD_REQUEST));
    }

    @NotNull
    public APIGatewayProxyResponseEvent notFound() {
        return APIGatewayProxyResponseEventFactory.decorate(new APIGatewayProxyResponseEvent().withStatusCode(HttpStatus.SC_NOT_FOUND));
    }

    @NotNull
    private APIGatewayProxyResponseEvent decorate(@NotNull final APIGatewayProxyResponseEvent event) {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "*");
        headers.put("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS");
        headers.put("Access-Control-Allow-Origin", "*");

        return event.withHeaders(headers);
    }
}
