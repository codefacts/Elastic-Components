package elasta.orm.jpa;

/**
 * Created by Jango on 10/8/2016.
 */
public class RelationTableInfo {
    private final String leftColumnValue;
    private final String rightColumnValue;

    public RelationTableInfo(String leftColumnValue, String rightColumnValue) {
        this.leftColumnValue = leftColumnValue;
        this.rightColumnValue = rightColumnValue;
    }

    public String getLeftColumnValue() {
        return leftColumnValue;
    }

    public String getRightColumnValue() {
        return rightColumnValue;
    }
}
