package elasta.orm;

/**
 * Created by Jango on 9/14/2016.
 */
final public class JoinSpec {
    private final String joinTable;
    private final String joinColumn;
    private final JoinType joinType;
    private final String joinTableAlias;
    private final RelationType relationType;
    private final RelationTable relationTable;

    public JoinSpec(String joinTable, String joinColumn, JoinType joinType, String joinTableAlias, RelationType relationType, RelationTable relationTable) {
        this.joinTable = joinTable;
        this.joinColumn = joinColumn;
        this.joinType = joinType;
        this.joinTableAlias = joinTableAlias;
        this.relationType = relationType;
        this.relationTable = relationTable;
    }

    public String getJoinTable() {
        return joinTable;
    }

    public String getJoinColumn() {
        return joinColumn;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public String getJoinTableAlias() {
        return joinTableAlias;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public RelationTable getRelationTable() {
        return relationTable;
    }
}
