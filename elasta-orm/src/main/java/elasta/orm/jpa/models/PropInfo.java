package elasta.orm.jpa.models;

/**
 * Created by Jango on 10/6/2016.
 */
public class PropInfo {
    private final String name;
    private final RelationInfo relationInfo;

    public PropInfo(String name, RelationInfo relationInfo) {
        this.name = name;
        this.relationInfo = relationInfo;
    }

    public String getName() {
        return name;
    }

    public RelationInfo getRelationInfo() {
        return relationInfo;
    }
}
