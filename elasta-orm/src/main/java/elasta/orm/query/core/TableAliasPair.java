package elasta.orm.query.core;

import java.util.Objects;

/**
 * Created by Jango on 17/02/09.
 */
final public class TableAliasPair {
    final String table;
    final String alias;

    public TableAliasPair(String table, String alias) {
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
    public String toString() {
        return "TableAliasPair{" +
            "dependentTable='" + table + '\'' +
            ", alias='" + alias + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableAliasPair that = (TableAliasPair) o;

        if (table != null ? !table.equals(that.table) : that.table != null) return false;
        return alias != null ? alias.equals(that.alias) : that.alias == null;

    }

    @Override
    public int hashCode() {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }
}
