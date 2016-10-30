package elasta.orm.jpa.models;

/**
 * Created by Jango on 10/6/2016.
 */
public class ChildModelInfo {
    private final String childModel;
    private final String joinField;
    private final String joinColumn;

    public ChildModelInfo(String childModel, String joinField, String joinColumn) {
        this.childModel = childModel;
        this.joinField = joinField;
        this.joinColumn = joinColumn;
    }

    public String getChildModel() {
        return childModel;
    }

    public String getJoinField() {
        return joinField;
    }

    public String getJoinColumn() {
        return joinColumn;
    }
}
