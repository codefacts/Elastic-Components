package elasta.orm.jpa.models;

/**
 * Created by Jango on 10/6/2016.
 */
public class ChildModelInfo {
    private final String childModel;
    private final String joinField;

    public ChildModelInfo(String childModel, String joinField) {
        this.childModel = childModel;
        this.joinField = joinField;
    }

    public String getChildModel() {
        return childModel;
    }

    public String getJoinField() {
        return joinField;
    }
}
