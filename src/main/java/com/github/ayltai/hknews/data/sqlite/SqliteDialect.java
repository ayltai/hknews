package com.github.ayltai.hknews.data.sqlite;

import java.sql.Types;

import org.springframework.lang.NonNull;

import org.hibernate.ScrollMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.unique.UniqueDelegate;
import org.hibernate.type.StandardBasicTypes;

public final class SqliteDialect extends Dialect {
    //region Constants

    private static final String INTEGER = "integer";
    private static final String BLOB    = "blob";
    private static final String QUOTE   = "quote";
    private static final String RANDOM  = "random";
    private static final String ROUND   = "round";
    private static final String SUBSTR  = "substr";

    //endregion

    private final LimitHandler   limitHandler;
    private final UniqueDelegate uniqueDelegate;

    public SqliteDialect() {
        this.registerColumnType(Types.BIT, SqliteDialect.INTEGER);
        this.registerColumnType(Types.TINYINT, "tinyint");
        this.registerColumnType(Types.SMALLINT, "smallint");
        this.registerColumnType(Types.BIGINT, "bigint");
        this.registerColumnType(Types.FLOAT, "float");
        this.registerColumnType(Types.REAL, "real");
        this.registerColumnType(Types.DOUBLE, "double");
        this.registerColumnType(Types.NUMERIC, "numeric");
        this.registerColumnType(Types.DECIMAL, "decimal");
        this.registerColumnType(Types.CHAR, "char");
        this.registerColumnType(Types.VARCHAR, "varchar");
        this.registerColumnType(Types.LONGVARCHAR, "longvarchar");
        this.registerColumnType(Types.DATE, "date");
        this.registerColumnType(Types.TIME, "time");
        this.registerColumnType(Types.TIMESTAMP, "timestamp");
        this.registerColumnType(Types.BINARY, SqliteDialect.BLOB);
        this.registerColumnType(Types.VARBINARY, SqliteDialect.BLOB);
        this.registerColumnType(Types.LONGVARBINARY, SqliteDialect.BLOB);
        this.registerColumnType(Types.BLOB, SqliteDialect.BLOB);
        this.registerColumnType(Types.CLOB, "clob");
        this.registerColumnType(Types.BOOLEAN, SqliteDialect.INTEGER);

        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", ""));
        this.registerFunction("mod", new SQLFunctionTemplate(StandardBasicTypes.STRING, "?1 % ?2"));
        this.registerFunction(SqliteDialect.QUOTE, new StandardSQLFunction(SqliteDialect.QUOTE, StandardBasicTypes.STRING));
        this.registerFunction(SqliteDialect.RANDOM, new NoArgSQLFunction(SqliteDialect.RANDOM, StandardBasicTypes.INTEGER));
        this.registerFunction(SqliteDialect.ROUND, new StandardSQLFunction(SqliteDialect.ROUND));
        this.registerFunction(SqliteDialect.SUBSTR, new StandardSQLFunction(SqliteDialect.SUBSTR, StandardBasicTypes.STRING));
        this.registerFunction("substring", new StandardSQLFunction(SqliteDialect.SUBSTR, StandardBasicTypes.STRING));
        this.registerFunction("trim", new SqliteAnsiTrimFunction());

        this.limitHandler   = new SqliteLimitHandler();
        this.uniqueDelegate = new SqliteUniqueDelegate(this);
    }

    @NonNull
    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new SqliteIdentityColumnSupport();
    }

    @Override
    public boolean canCreateSchema() {
        return false;
    }

    @Override
    public boolean doesReadCommittedCauseWritersToBlockReaders() {
        return true;
    }

    @Override
    public boolean doesRepeatableReadCauseReadersToBlockWriters() {
        return true;
    }

    @NonNull
    @Override
    public ScrollMode defaultScrollMode() {
        return ScrollMode.FORWARD_ONLY;
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public boolean hasAlterTable() {
        return false;
    }

    @Override
    public boolean qualifyIndexName() {
        return false;
    }

    @Override
    public boolean supportsCurrentTimestampSelection() {
        return true;
    }

    @Override
    public boolean isCurrentTimestampSelectStringCallable() {
        return false;
    }

    @NonNull
    @Override
    public String getCurrentTimestampSelectString() {
        return "select current_timestamp";
    }

    @Override
    public boolean supportsCascadeDelete() {
        return false;
    }

    @Override
    public boolean supportsCommentOn() {
        return true;
    }

    @Override
    public boolean supportsIfExistsBeforeTableName() {
        return true;
    }

    @Override
    public boolean supportsOuterJoinForUpdate() {
        return false;
    }

    @Override
    public boolean supportsTupleDistinctCounts() {
        return false;
    }

    @Override
    public boolean supportsUnionAll() {
        return true;
    }

    @NonNull
    @Override
    public LimitHandler getLimitHandler() {
        return this.limitHandler;
    }

    @NonNull
    @Override
    public UniqueDelegate getUniqueDelegate() {
        return this.uniqueDelegate;
    }

    @NonNull
    @Override
    public String getAddColumnString() {
        return "add column";
    }

    @NonNull
    @Override
    public String getAddForeignKeyConstraintString(final String constraintName, final String[] foreignKey, final String referencedTable, final String[] primaryKey, final boolean referencesPrimaryKey) {
        throw new UnsupportedOperationException("No add foreign key syntax supported");
    }

    @NonNull
    @Override
    public String getAddPrimaryKeyConstraintString(final String constraintName) {
        throw new UnsupportedOperationException("No add primary key syntax supported");
    }

    @NonNull
    @Override
    public String getDropForeignKeyString() {
        throw new UnsupportedOperationException("No drop foreign key syntax supported");
    }

    @NonNull
    @Override
    public String getForUpdateString() {
        return "";
    }

    @Override
    public int getInExpressionCountLimit() {
        return 1000;
    }

    @NonNull
    @Override
    public String getSelectGUIDString() {
        return "select hex(randomblob(16))";
    }
}
