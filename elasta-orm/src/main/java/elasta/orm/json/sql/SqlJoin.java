package elasta.orm.json.sql;

import elasta.orm.json.sql.core.JoinType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 3/7/2017.
 */
final public class SqlJoin {
    final JoinType joinType;
    final String joinTable;
    final Optional<String> alias;
    final List<SqlJoinColumn> sqlJoinColumns;

    public SqlJoin(JoinType joinType, String joinTable, Optional<String> alias, List<SqlJoinColumn> sqlJoinColumns) {
        Objects.requireNonNull(joinType);
        Objects.requireNonNull(joinTable);
        Objects.requireNonNull(alias);
        Objects.requireNonNull(sqlJoinColumns);
        this.joinType = joinType;
        this.joinTable = joinTable;
        this.alias = alias;
        this.sqlJoinColumns = sqlJoinColumns;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public List<SqlJoinColumn> getSqlJoinColumns() {
        return sqlJoinColumns;
    }

    public String getJoinTable() {
        return joinTable;
    }

    public Optional<String> getAlias() {
        return alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SqlJoin sqlJoin = (SqlJoin) o;

        if (joinType != sqlJoin.joinType) return false;
        if (joinTable != null ? !joinTable.equals(sqlJoin.joinTable) : sqlJoin.joinTable != null) return false;
        if (alias != null ? !alias.equals(sqlJoin.alias) : sqlJoin.alias != null) return false;
        return sqlJoinColumns != null ? sqlJoinColumns.equals(sqlJoin.sqlJoinColumns) : sqlJoin.sqlJoinColumns == null;
    }

    @Override
    public int hashCode() {
        int result = joinType != null ? joinType.hashCode() : 0;
        result = 31 * result + (joinTable != null ? joinTable.hashCode() : 0);
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        result = 31 * result + (sqlJoinColumns != null ? sqlJoinColumns.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SqlJoin{" +
            "joinType=" + joinType +
            ", joinTable='" + joinTable + '\'' +
            ", alias=" + alias +
            ", sqlJoinColumns=" + sqlJoinColumns +
            '}';
    }
}
