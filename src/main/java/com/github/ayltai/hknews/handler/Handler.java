package com.github.ayltai.hknews.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Handler<I, O> implements RequestHandler<I, O> {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Logger LOGGER = LoggerFactory.getLogger(Handler.class);

    @Override
    public abstract O handleRequest(@NotNull I event, @NotNull Context context);

    protected final void log(@NotNull final Throwable throwable, @NotNull final Object event, @NotNull final Context context) {
        this.log(event, context);

        if (Handler.LOGGER.isErrorEnabled()) Handler.LOGGER.error("EXCEPTION: {}", Handler.GSON.toJson(throwable));
    }

    private void log(@NotNull final Object event, @NotNull final Context context) {
        if (Handler.LOGGER.isInfoEnabled()) {
            Handler.LOGGER.info("ENV: {}", Handler.GSON.toJson(System.getenv()));
            Handler.LOGGER.info("CONTEXT: {}", Handler.GSON.toJson(context));
            Handler.LOGGER.info("EVENT: {}", Handler.GSON.toJson(event));
            Handler.LOGGER.info("EVENT TYPE: {}", event.getClass().getSimpleName());
        }
    }
}
