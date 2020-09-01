package com.github.ayltai.hknews.data.sqlite;

import org.springframework.lang.NonNull;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.unique.DefaultUniqueDelegate;
import org.hibernate.mapping.Column;

final class SqliteUniqueDelegate extends DefaultUniqueDelegate {
    SqliteUniqueDelegate(@NonNull final Dialect dialect) {
        super(dialect);
    }

    @NonNull
    @Override
    public String getColumnDefinitionUniquenessFragment(final Column column) {
        return " unique";
    }
}
