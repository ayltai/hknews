package com.github.ayltai.hknews.handler;

import org.jetbrains.annotations.NotNull;

public final class OrientalDailyParserHandler extends ParserHandler {
    @NotNull
    @Override
    protected String getSourceName() {
        return "東方日報";
    }
}
