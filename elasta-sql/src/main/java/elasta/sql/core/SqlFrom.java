package elasta.sql.core;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 3/7/2017.
 */
final public class SqlFrom {
    final String table;
    final String alias;

    public SqlFrom(String table, String alias) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(alias);
        this.table = table;
        this.alias = alias;
    }

    public String getTable() {
        return table;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SqlFrom sqlFrom = (SqlFrom) o;

        if (table != null ? !table.equals(sqlFrom.table) : sqlFrom.table != null) return false;
        return alias != null ? alias.equals(sqlFrom.alias) : sqlFrom.alias == null;
    }

    @Override
    public int hashCode() {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SqlFrom{" +
            "table='" + table + '\'' +
            ", alias=" + alias +
            '}';
    }
}
