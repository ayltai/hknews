package com.github.ayltai.hknews.handler;

import org.jetbrains.annotations.NotNull;

public final class HeadlineRealtimeParserHandler extends ParserHandler {
    @NotNull
    @Override
    protected String getSourceName() {
        return "頭條即時";
    }
}
