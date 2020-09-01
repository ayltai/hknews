package com.github.ayltai.hknews.data.sqlite;

import org.springframework.lang.NonNull;

import org.hibernate.MappingException;
import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

public final class SqliteIdentityColumnSupport extends IdentityColumnSupportImpl {
    @Override
    public boolean supportsIdentityColumns() {
        return true;
    }

    @NonNull
    @Override
    public String getIdentitySelectString(final String table, final String column, final int type) throws MappingException {
        return "select last_insert_rowid()";
    }

    @Override
    public String getIdentityColumnString(final int type) throws MappingException {
        return "integer";
    }
}
