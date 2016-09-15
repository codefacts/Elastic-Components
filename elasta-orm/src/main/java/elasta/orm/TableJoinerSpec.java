package elasta.orm;

/**
 * Created by Jango on 9/15/2016.
 */
public class TableJoinerSpec {
    private final String column;
    private final String joinTableAlias;
    private final JoinType joinType;

    public TableJoinerSpec(String column, String joinTableAlias, JoinType joinType) {
        this.column = column;
        this.joinTableAlias = joinTableAlias;
        this.joinType = joinType;
    }

    public String getColumn() {
        return column;
    }

    public String getJoinTableAlias() {
        return joinTableAlias;
    }

    public JoinType getJoinType() {
        return joinType;
    }
}
