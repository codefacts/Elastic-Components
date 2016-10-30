package elasta.orm.jpa.models;

public class ChildModelInfoBuilder {
    private String childModel;
    private String joinField;
    private String joinColumn;

    public ChildModelInfoBuilder setChildModel(String childModel) {
        this.childModel = childModel;
        return this;
    }

    public ChildModelInfoBuilder setJoinField(String joinField) {
        this.joinField = joinField;
        return this;
    }

    public ChildModelInfoBuilder setJoinColumn(String joinColumn) {
        this.joinColumn = joinColumn;
        return this;
    }

    public ChildModelInfo createJoinTableInfo() {
        return new ChildModelInfo(childModel, joinField, joinColumn);
    }
}