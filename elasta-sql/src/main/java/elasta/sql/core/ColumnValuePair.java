package elasta.sql.core;

import java.util.Objects;

/**
 * Created by Jango on 17/02/07.
 */
final public class ColumnValuePair {
    final String primaryColumn;
    final Object value;

    public ColumnValuePair(String primaryColumn, Object value) {
        Objects.requireNonNull(primaryColumn);
        Objects.requireNonNull(value);
        this.primaryColumn = primaryColumn;
        this.value = value;
    }

    public String getPrimaryColumn() {
        return primaryColumn;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ColumnValuePair{" +
            "primaryColumn='" + primaryColumn + '\'' +
            ", value=" + value +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnValuePair that = (ColumnValuePair) o;

        if (primaryColumn != null ? !primaryColumn.equals(that.primaryColumn) : that.primaryColumn != null)
            return false;
        return value != null ? value.equals(that.value) : that.value == null;

    }

    @Override
    public int hashCode() {
        int result = primaryColumn != null ? primaryColumn.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
