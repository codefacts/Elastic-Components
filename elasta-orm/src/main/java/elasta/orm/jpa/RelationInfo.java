package elasta.orm.jpa;

import elasta.orm.json.core.RelationTable;
import elasta.orm.json.core.RelationType;

/**
 * Created by Jango on 10/6/2016.
 */
public class RelationInfo {
    private final RelationType relationType;
    private final RelationTable relationTable;
    private final JoinTableInfo joinTableInfo;

    public RelationInfo(RelationType relationType, RelationTable relationTable, JoinTableInfo joinTableInfo) {
        this.relationType = relationType;
        this.relationTable = relationTable;
        this.joinTableInfo = joinTableInfo;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public RelationTable getRelationTable() {
        return relationTable;
    }

    public JoinTableInfo getJoinTableInfo() {
        return joinTableInfo;
    }
}
