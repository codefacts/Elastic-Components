package elasta.orm.jpa.models;

import elasta.orm.json.core.RelationTable;
import elasta.orm.json.core.RelationType;

public class RelationInfoBuilder {
    private RelationType relationType;
    private RelationTable relationTable;
    private JoinTableInfo joinTableInfo;

    public RelationInfoBuilder setRelationType(RelationType relationType) {
        this.relationType = relationType;
        return this;
    }

    public RelationInfoBuilder setRelationTable(RelationTable relationTable) {
        this.relationTable = relationTable;
        return this;
    }

    public RelationInfoBuilder setJoinTableInfo(JoinTableInfo joinTableInfo) {
        this.joinTableInfo = joinTableInfo;
        return this;
    }

    public RelationInfo createRelationInfo() {
        return new RelationInfo(relationType, relationTable, joinTableInfo);
    }
}