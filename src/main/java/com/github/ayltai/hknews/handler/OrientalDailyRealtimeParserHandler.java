package com.github.ayltai.hknews.handler;

import org.jetbrains.annotations.NotNull;

public final class OrientalDailyRealtimeParserHandler extends ParserHandler {
    @NotNull
    @Override
    protected String getSourceName() {
        return "東方即時";
    }
}
