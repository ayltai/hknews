package com.github.ayltai.hknews.data.sqlite;

import org.springframework.lang.NonNull;

import org.hibernate.dialect.function.AbstractAnsiTrimEmulationFunction;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

final class SqliteAnsiTrimFunction extends AbstractAnsiTrimEmulationFunction {
    @NonNull
    @Override
    protected SQLFunction resolveBothSpaceTrimFunction() {
        return new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?1)");
    }

    @NonNull
    @Override
    protected SQLFunction resolveBothSpaceTrimFromFunction() {
        return new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?2)");
    }

    @NonNull
    @Override
    protected SQLFunction resolveLeadingSpaceTrimFunction() {
        return new SQLFunctionTemplate(StandardBasicTypes.STRING, "ltrim(?1)");
    }

    @NonNull
    @Override
    protected SQLFunction resolveTrailingSpaceTrimFunction() {
        return new SQLFunctionTemplate(StandardBasicTypes.STRING, "rtrim(?1)");
    }

    @NonNull
    @Override
    protected SQLFunction resolveBothTrimFunction() {
        return new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?1, ?2)");
    }

    @NonNull
    @Override
    protected SQLFunction resolveLeadingTrimFunction() {
        return new SQLFunctionTemplate(StandardBasicTypes.STRING, "ltrim(?1, ?2)");
    }

    @NonNull
    @Override
    protected SQLFunction resolveTrailingTrimFunction() {
        return new SQLFunctionTemplate(StandardBasicTypes.STRING, "rtrim(?1, ?2)");
    }
}
