package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.util.Collection;

import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.net.ContentServiceFactory;
import com.github.ayltai.hknews.service.SourceService;

import org.jetbrains.annotations.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
    public abstract Collection<Item> getItems(@NotNull String categoryName);

    @NotNull
    public abstract Item updateItem(@NotNull Item item) throws IOException;
}
