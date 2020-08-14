package com.github.ayltai.hknews.parser;

import org.springframework.lang.NonNull;

public interface ParserFactory {
    @NonNull
    Parser create(@NonNull String sourceName);
}
