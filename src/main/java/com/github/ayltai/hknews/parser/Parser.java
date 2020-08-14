package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.util.Collection;

import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ContentServiceFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Parser {
    @Getter
    @NonNull
    protected final String sourceName;

    @NonNull
    protected final SourceRepository sourceRepository;

    @NonNull
    protected final ContentServiceFactory contentServiceFactory;

    @NonNull
    public abstract Collection<Item> getItems(@NonNull String categoryName);

    @NonNull
    public abstract Item updateItem(@NonNull Item item) throws IOException;
}
