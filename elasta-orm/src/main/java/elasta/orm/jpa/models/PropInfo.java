package elasta.orm.jpa.models;

/**
 * Created by Jango on 10/6/2016.
 */
public class PropInfo {
    private final String name;
    private final String column;
    private final RelationInfo relationInfo;

    public PropInfo(String name, String column, RelationInfo relationInfo) {
        this.name = name;
        this.column = column;
        this.relationInfo = relationInfo;
    }

    public boolean hasRelation() {

        return relationInfo != null;
    }

    public boolean hasColumn() {
        return column != null;
    }

    public String getName() {
        return name;
    }

    public String getColumn() {
        return column;
    }

    public RelationInfo getRelationInfo() {
        return relationInfo;
    }
}
