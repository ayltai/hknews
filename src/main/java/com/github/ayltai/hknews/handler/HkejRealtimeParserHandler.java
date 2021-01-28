package com.github.ayltai.hknews.handler;

import org.jetbrains.annotations.NotNull;

public final class HkejRealtimeParserHandler extends ParserHandler {
    @NotNull
    @Override
    protected String getSourceName() {
        return "信報即時";
    }
}
