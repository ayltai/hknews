package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;
import com.google.gson.Gson;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Parser {
    @Getter
    @NotNull
    protected final String sourceName;

    @NotNull
    protected final SourceService sourceService;

    @NotNull
    protected final ContentServiceFactory contentServiceFactory;

    @NotNull
    protected final LambdaLogger logger;

    @NotNull
    public Collection<Item> getItems(@NotNull final String categoryName) {
        return this.sourceService
            .getSources(this.sourceName, categoryName)
            .stream()
            .map(source -> {
                try {
                    return this.getItems(source);
                } catch (final ProtocolException e) {
                    if (e.getMessage().startsWith("Too many follow-up requests")) this.logger.log(new Gson().toJson(e));
                } catch (final SSLHandshakeException | SocketTimeoutException e) {
                    this.logger.log(new Gson().toJson(e));
                } catch (final SSLException e) {
                    if (e.getMessage().equals("Connection reset")) this.logger.log(new Gson().toJson(e));
                } catch (final IOException e) {
                    this.logger.log(this.getClass().getSimpleName());
                    this.logger.log(new Gson().toJson(e));
                }

                return Collections.<Item>emptyList();
            })
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    @NotNull
    protected abstract Collection<Item> getItems(@NotNull Source source) throws ProtocolException, SSLHandshakeException, SocketTimeoutException, SSLException, IOException;

    @NotNull
    public abstract Item updateItem(@NotNull Item item) throws IOException;
}
