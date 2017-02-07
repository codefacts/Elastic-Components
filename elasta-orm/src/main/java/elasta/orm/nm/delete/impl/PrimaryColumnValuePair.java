package elasta.orm.nm.delete.impl;

import java.util.Objects;

/**
 * Created by Jango on 17/02/07.
 */
final public class PrimaryColumnValuePair {
    final String primaryColumn;
    final Object value;

    public PrimaryColumnValuePair(String primaryColumn, Object value) {
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
        return "PrimaryColumnValuePair{" +
            "primaryColumn='" + primaryColumn + '\'' +
            ", value=" + value +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimaryColumnValuePair that = (PrimaryColumnValuePair) o;

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
