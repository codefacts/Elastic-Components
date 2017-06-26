package elasta.sql.core;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 3/7/2017.
 */
final public class SqlSelection {
    final String column;
    final String alias;

    public SqlSelection(String column, String alias) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(alias);
        this.column = column;
        this.alias = alias;
    }

    public String getColumn() {
        return column;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SqlSelection that = (SqlSelection) o;

        if (column != null ? !column.equals(that.column) : that.column != null) return false;
        return alias != null ? alias.equals(that.alias) : that.alias == null;
    }

    @Override
    public int hashCode() {
        int result = column != null ? column.hashCode() : 0;
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return alias + "." + column;
    }
}
