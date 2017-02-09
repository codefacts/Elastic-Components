package elasta.orm.nm.query.impl;

import elasta.orm.json.sql.core.JoinType;

import java.util.Objects;

/**
 * Created by Jango on 17/02/09.
 */
final public class JoinData {
    final JoinType joinType;
    final String table;
    final String alias;
    final String parentColumn;
    final String column;

    public JoinData(JoinType joinType, String table, String alias, String parentColumn, String column) {
        Objects.requireNonNull(joinType);
        Objects.requireNonNull(table);
        Objects.requireNonNull(alias);
        Objects.requireNonNull(parentColumn);
        Objects.requireNonNull(column);
        this.joinType = joinType;
        this.table = table;
        this.alias = alias;
        this.parentColumn = parentColumn;
        this.column = column;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public String getTable() {
        return table;
    }

    public String getAlias() {
        return alias;
    }

    public String getParentColumn() {
        return parentColumn;
    }

    public String getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinData joinData = (JoinData) o;

        if (joinType != joinData.joinType) return false;
        if (table != null ? !table.equals(joinData.table) : joinData.table != null) return false;
        if (alias != null ? !alias.equals(joinData.alias) : joinData.alias != null) return false;
        if (parentColumn != null ? !parentColumn.equals(joinData.parentColumn) : joinData.parentColumn != null)
            return false;
        return column != null ? column.equals(joinData.column) : joinData.column == null;

    }

    @Override
    public int hashCode() {
        int result = joinType != null ? joinType.hashCode() : 0;
        result = 31 * result + (table != null ? table.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + (parentColumn != null ? parentColumn.hashCode() : 0);
        result = 31 * result + (column != null ? column.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JoinData{" +
            "joinType=" + joinType +
            ", table='" + table + '\'' +
            ", alias='" + alias + '\'' +
            ", parentColumn='" + parentColumn + '\'' +
            ", column='" + column + '\'' +
            '}';
    }
}
