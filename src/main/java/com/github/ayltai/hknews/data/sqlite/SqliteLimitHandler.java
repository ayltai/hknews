package com.github.ayltai.hknews.data.sqlite;

import org.springframework.lang.NonNull;

import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;

final class SqliteLimitHandler extends AbstractLimitHandler {
    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean bindLimitParametersInReverseOrder() {
        return true;
    }

    @NonNull
    @Override
    public String processSql(@NonNull final String sql, @NonNull final RowSelection selection) {
        return sql + (LimitHelper.hasFirstRow(selection) ? " limit ? offset ?" : " limit ?");
    }
}
