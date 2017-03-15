package elasta.sql.core;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 3/7/2017.
 */
final public class SqlJoinColumn {
    final String joinTableColumn;
    final String parentTableColumn;
    final Optional<String> parentTableAlias;

    public SqlJoinColumn(String joinTableColumn, String parentTableColumn, Optional<String> parentTableAlias) {
        Objects.requireNonNull(joinTableColumn);
        Objects.requireNonNull(parentTableColumn);
        Objects.requireNonNull(parentTableAlias);
        this.joinTableColumn = joinTableColumn;
        this.parentTableColumn = parentTableColumn;
        this.parentTableAlias = parentTableAlias;
    }

    public String getJoinTableColumn() {
        return joinTableColumn;
    }

    public String getParentTableColumn() {
        return parentTableColumn;
    }

    public Optional<String> getParentTableAlias() {
        return parentTableAlias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SqlJoinColumn that = (SqlJoinColumn) o;

        if (joinTableColumn != null ? !joinTableColumn.equals(that.joinTableColumn) : that.joinTableColumn != null)
            return false;
        if (parentTableColumn != null ? !parentTableColumn.equals(that.parentTableColumn) : that.parentTableColumn != null)
            return false;
        return parentTableAlias != null ? parentTableAlias.equals(that.parentTableAlias) : that.parentTableAlias == null;
    }

    @Override
    public int hashCode() {
        int result = joinTableColumn != null ? joinTableColumn.hashCode() : 0;
        result = 31 * result + (parentTableColumn != null ? parentTableColumn.hashCode() : 0);
        result = 31 * result + (parentTableAlias != null ? parentTableAlias.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SqlJoinColumn{" +
            "joinTableColumn='" + joinTableColumn + '\'' +
            ", parentTableColumn='" + parentTableColumn + '\'' +
            ", parentTableAlias=" + parentTableAlias +
            '}';
    }
}
