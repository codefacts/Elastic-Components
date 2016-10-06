package elasta.orm.jpa;

public class PropInfoBuilder {
    private String name;
    private RelationInfo relationInfo;

    public PropInfoBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PropInfoBuilder setRelationInfo(RelationInfo relationInfo) {
        this.relationInfo = relationInfo;
        return this;
    }

    public PropInfo createPropInfo() {
        return new PropInfo(name, relationInfo);
    }
}