package com.github.ayltai.hknews.handler;

import org.jetbrains.annotations.NotNull;

public final class HkejParserHandler extends ParserHandler {
    @NotNull
    @Override
    protected String getSourceName() {
        return "信報";
    }
}
