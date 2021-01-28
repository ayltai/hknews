package com.github.ayltai.hknews.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

public abstract class Handler<I, O> implements RequestHandler<I, O> {
    protected static final Gson GSON = new Gson();

    @Override
    public abstract O handleRequest(@NotNull I event, @NotNull Context context);

    protected final void log(@NotNull final Throwable throwable, @NotNull final Object event, @NotNull final Context context) {
        this.log(event, context);

        context.getLogger().log(String.format("EXCEPTION: %s", Handler.GSON.toJson(throwable)));
    }

    protected void log(@NotNull final Object event, @NotNull final Context context) {
        final LambdaLogger logger = context.getLogger();

        logger.log(String.format("ENV: %s", Handler.GSON.toJson(System.getenv())));
        logger.log(String.format("CONTEXT: %s", Handler.GSON.toJson(context)));
        logger.log(String.format("EVENT: %s", Handler.GSON.toJson(event)));
        logger.log(String.format("EVENT TYPE: %s", event.getClass().getSimpleName()));
    }
}
