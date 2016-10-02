package elasta.orm.core;

public class JoinInfoBuilder {
    private RelationType relationType;
    private RelationTable relationTable;
    private ModelSpec joinModel;
    private String joinProp;

    public JoinInfoBuilder setRelationType(RelationType relationType) {
        this.relationType = relationType;
        return this;
    }

    public JoinInfoBuilder setRelationTable(RelationTable relationTable) {
        this.relationTable = relationTable;
        return this;
    }

    public JoinInfoBuilder setJoinModel(ModelSpec joinModel) {
        this.joinModel = joinModel;
        return this;
    }

    public JoinInfoBuilder setJoinProp(String joinProp) {
        this.joinProp = joinProp;
        return this;
    }

    public JoinInfo createJoinInfo() {
        return new JoinInfo(relationType, relationTable, joinModel, joinProp);
    }
}