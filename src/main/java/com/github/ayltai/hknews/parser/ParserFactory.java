package com.github.ayltai.hknews.parser;

import org.jetbrains.annotations.NotNull;

public interface ParserFactory {
    @NotNull
    Parser create(@NotNull String sourceName);
}
