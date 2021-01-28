package com.github.ayltai.hknews.parser;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import org.jetbrains.annotations.NotNull;

public interface ParserFactory {
    @NotNull
    Parser create(@NotNull String sourceName, @NotNull LambdaLogger logger);
}
