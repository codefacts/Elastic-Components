package elasta.orm.query.core;

import java.util.Objects;

/**
 * Created by Jango on 17/02/09.
 */
final public class ColumnAliasPair {
    final String alias;
    final String column;

    public ColumnAliasPair(String alias, String column) {
        Objects.requireNonNull(alias);
        Objects.requireNonNull(column);
        this.alias = alias;
        this.column = column;
    }

    public String getAlias() {
        return alias;
    }

    public String getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnAliasPair that = (ColumnAliasPair) o;

        if (alias != null ? !alias.equals(that.alias) : that.alias != null) return false;
        return column != null ? column.equals(that.column) : that.column == null;

    }

    @Override
    public int hashCode() {
        int result = alias != null ? alias.hashCode() : 0;
        result = 31 * result + (column != null ? column.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ColumnAliasPair{" +
            "alias='" + alias + '\'' +
            ", column='" + column + '\'' +
            '}';
    }
}
