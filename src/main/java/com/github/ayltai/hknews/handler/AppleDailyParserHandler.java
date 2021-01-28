package com.github.ayltai.hknews.handler;

import org.jetbrains.annotations.NotNull;

public final class AppleDailyParserHandler extends ParserHandler {
    @NotNull
    @Override
    protected String getSourceName() {
        return "蘋果日報";
    }
}
