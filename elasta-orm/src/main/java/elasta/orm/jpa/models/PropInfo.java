package elasta.orm.jpa.models;

import elasta.commons.Utils;
import elasta.orm.json.core.RelationType;

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

    public boolean isSingular() {
        return Utils.not(isPlural());
    }

    public boolean isPlural() {
        return relationInfo != null && (relationInfo.getRelationType() == RelationType.ONE_TO_MANY || relationInfo.getRelationType() == RelationType.MANY_TO_MANY);
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

    public boolean hasRelationTable() {
        return relationInfo.getRelationTable() != null;
    }
}
