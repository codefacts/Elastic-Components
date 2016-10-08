package elasta.orm.jpa.models;

/**
 * Created by Jango on 10/6/2016.
 */
public class JoinTableInfo {
    private final String joinModel;
    private final String joinField;

    public JoinTableInfo(String joinModel, String joinField) {
        this.joinModel = joinModel;
        this.joinField = joinField;
    }

    public String getJoinModel() {
        return joinModel;
    }

    public String getJoinField() {
        return joinField;
    }
}
