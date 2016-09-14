package elasta.orm;

/**
 * Created by Jango on 9/14/2016.
 */
final public class JoinTuple {
    private final String alias;
    private final String joinTable;
    private final String joinTableAlias;
    private final String column;
    private final String joinColumn;
    private final JoinType joinType;

    public JoinTuple(String alias, String joinTable, String joinTableAlias, String column, String joinColumn, JoinType joinType) {
        this.alias = alias;
        this.joinTable = joinTable;
        this.joinTableAlias = joinTableAlias;
        this.column = column;
        this.joinColumn = joinColumn;
        this.joinType = joinType;
    }

    public String getAlias() {
        return alias;
    }

    public String getJoinTable() {
        return joinTable;
    }

    public String getJoinTableAlias() {
        return joinTableAlias;
    }

    public String getColumn() {
        return column;
    }

    public String getJoinColumn() {
        return joinColumn;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinTuple joinTuple = (JoinTuple) o;

        if (alias != null ? !alias.equals(joinTuple.alias) : joinTuple.alias != null) return false;
        if (joinTable != null ? !joinTable.equals(joinTuple.joinTable) : joinTuple.joinTable != null) return false;
        if (joinTableAlias != null ? !joinTableAlias.equals(joinTuple.joinTableAlias) : joinTuple.joinTableAlias != null)
            return false;
        if (joinColumn != null ? !joinColumn.equals(joinTuple.joinColumn) : joinTuple.joinColumn != null) return false;
        return joinType == joinTuple.joinType;

    }

    @Override
    public int hashCode() {
        int result = alias != null ? alias.hashCode() : 0;
        result = 31 * result + (joinTable != null ? joinTable.hashCode() : 0);
        result = 31 * result + (joinTableAlias != null ? joinTableAlias.hashCode() : 0);
        result = 31 * result + (joinColumn != null ? joinColumn.hashCode() : 0);
        result = 31 * result + (joinType != null ? joinType.hashCode() : 0);
        return result;
    }
}
