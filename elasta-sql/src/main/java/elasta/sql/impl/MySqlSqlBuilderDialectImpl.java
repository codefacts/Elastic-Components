package elasta.sql.impl;

import elasta.sql.SqlBuilderDialect;

import java.util.Objects;

/**
 * Created by sohan on 3/14/2017.
 */
final public class MySqlSqlBuilderDialectImpl implements SqlBuilderDialect {

    public String table(String table, String alias) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(alias);
        return "`" + table + "`" + " " + alias;
    }

    @Override
    public String table(String table) {
        return "`" + table + "`";
    }

    @Override
    public String nullValue() {
        return "null";
    }

    public String column(String column, String alias) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(alias);
        return alias + "." + "`" + column + "`";
    }

    @Override
    public String column(String column) {
        return "`" + column + "`";
    }
}
