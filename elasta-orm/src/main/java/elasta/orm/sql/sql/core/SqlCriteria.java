package elasta.orm.sql.sql.core;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 3/8/2017.
 */
final public class SqlCriteria {
    final String column;
    final Object value;
    final Optional<String> alias;

    public SqlCriteria(String column, Object value, Optional<String> alias) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        Objects.requireNonNull(alias);
        this.column = column;
        this.value = value;
        this.alias = alias;
    }

    public String getColumn() {
        return column;
    }

    public Object getValue() {
        return value;
    }

    public Optional<String> getAlias() {
        return alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SqlCriteria that = (SqlCriteria) o;

        if (column != null ? !column.equals(that.column) : that.column != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        return alias != null ? alias.equals(that.alias) : that.alias == null;
    }

    @Override
    public int hashCode() {
        int result = column != null ? column.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SqlCriteria{" +
            "column='" + column + '\'' +
            ", value=" + value +
            ", alias=" + alias +
            '}';
    }
}
