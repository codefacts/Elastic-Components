package elasta.orm;

final public class JoinTupleBuilder {
    private String alias;
    private String joinTable;
    private String joinTableAlias;
    private String column;
    private String joinColumn;
    private JoinType joinType;

    public JoinTupleBuilder setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public JoinTupleBuilder setJoinTable(String joinTable) {
        this.joinTable = joinTable;
        return this;
    }

    public JoinTupleBuilder setJoinTableAlias(String joinTableAlias) {
        this.joinTableAlias = joinTableAlias;
        return this;
    }

    public JoinTupleBuilder setColumn(String column) {
        this.column = column;
        return this;
    }

    public JoinTupleBuilder setJoinColumn(String joinColumn) {
        this.joinColumn = joinColumn;
        return this;
    }

    public JoinTupleBuilder setJoinType(JoinType joinType) {
        this.joinType = joinType;
        return this;
    }

    public JoinTuple createJoinTuple() {
        return new JoinTuple(alias, joinTable, joinTableAlias, column, joinColumn, joinType);
    }
}