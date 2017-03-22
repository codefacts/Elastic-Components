package elasta.orm.query.expression.impl;

import elasta.orm.upsert.ColumnToColumnMapping;import elasta.sql.core.JoinType;

import java.util.List;
import java.util.Objects;

/**
 * Created by Jango on 17/02/09.
 */
final public class JoinData {
    final String parentAlias;
    final JoinType joinType;
    final String table;
    final String alias;
    final List<ColumnToColumnMapping> columnToColumnMappings;

    public JoinData(String parentAlias, JoinType joinType, String table, String alias, List<ColumnToColumnMapping> columnToColumnMappings) {
        Objects.requireNonNull(parentAlias);
        Objects.requireNonNull(joinType);
        Objects.requireNonNull(table);
        Objects.requireNonNull(alias);
        Objects.requireNonNull(columnToColumnMappings);
        this.parentAlias = parentAlias;
        this.joinType = joinType;
        this.table = table;
        this.alias = alias;
        this.columnToColumnMappings = columnToColumnMappings;
    }

    public String getParentAlias() {
        return parentAlias;
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

    public List<ColumnToColumnMapping> getColumnToColumnMappings() {
        return columnToColumnMappings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinData joinData = (JoinData) o;

        if (parentAlias != null ? !parentAlias.equals(joinData.parentAlias) : joinData.parentAlias != null)
            return false;
        if (joinType != joinData.joinType) return false;
        if (table != null ? !table.equals(joinData.table) : joinData.table != null) return false;
        if (alias != null ? !alias.equals(joinData.alias) : joinData.alias != null) return false;
        return columnToColumnMappings != null ? columnToColumnMappings.equals(joinData.columnToColumnMappings) : joinData.columnToColumnMappings == null;

    }

    @Override
    public int hashCode() {
        int result = parentAlias != null ? parentAlias.hashCode() : 0;
        result = 31 * result + (joinType != null ? joinType.hashCode() : 0);
        result = 31 * result + (table != null ? table.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + (columnToColumnMappings != null ? columnToColumnMappings.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JoinData{" +
            "parentAlias='" + parentAlias + '\'' +
            ", joinType=" + joinType +
            ", dependentTable='" + table + '\'' +
            ", alias='" + alias + '\'' +
            ", columnToColumnMappings=" + columnToColumnMappings +
            '}';
    }
}
