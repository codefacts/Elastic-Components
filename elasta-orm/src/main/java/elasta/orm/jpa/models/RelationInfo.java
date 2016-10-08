package elasta.orm.jpa.models;

import elasta.orm.json.core.RelationTable;
import elasta.orm.json.core.RelationType;

/**
 * Created by Jango on 10/6/2016.
 */
public class RelationInfo {
    private final RelationType relationType;
    private final RelationTable relationTable;
    private final ChildModelInfo childModelInfo;

    public RelationInfo(RelationType relationType, RelationTable relationTable, ChildModelInfo childModelInfo) {
        this.relationType = relationType;
        this.relationTable = relationTable;
        this.childModelInfo = childModelInfo;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public RelationTable getRelationTable() {
        return relationTable;
    }

    public ChildModelInfo getJoinModelInfo() {
        return childModelInfo;
    }
}
