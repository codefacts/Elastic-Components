package elasta.orm.jpa.models;

public class ChildModelInfoBuilder {
    private String childModel;
    private String joinField;

    public ChildModelInfoBuilder setChildModel(String childModel) {
        this.childModel = childModel;
        return this;
    }

    public ChildModelInfoBuilder setJoinField(String joinField) {
        this.joinField = joinField;
        return this;
    }

    public ChildModelInfo createJoinTableInfo() {
        return new ChildModelInfo(childModel, joinField);
    }
}