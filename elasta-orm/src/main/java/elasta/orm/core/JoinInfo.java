package elasta.orm.core;

/**
 * Created by Jango on 10/2/2016.
 */
public class JoinInfo {
    private final RelationType relationType;
    private final RelationTable relationTable;
    private final ModelSpec joinModel;
    private final String joinProp;

    public JoinInfo(RelationType relationType, RelationTable relationTable, ModelSpec joinModel, String joinProp) {
        this.relationType = relationType;
        this.relationTable = relationTable;
        this.joinModel = joinModel;
        this.joinProp = joinProp;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public RelationTable getRelationTable() {
        return relationTable;
    }

    public ModelSpec getJoinModel() {
        return joinModel;
    }

    public String getJoinProp() {
        return joinProp;
    }
}
