package com.github.ayltai.hknews.handler;

import org.jetbrains.annotations.NotNull;

public final class HketParserHandler extends ParserHandler {
    @NotNull
    @Override
    protected String getSourceName() {
        return "經濟日報";
    }
}
