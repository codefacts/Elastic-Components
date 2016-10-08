package elasta.orm.jpa;

/**
 * Created by Jango on 10/8/2016.
 */
public class RelationTableIdPair {
    private final String relationTable;
    private final Object leftColumnValue;
    private final Object rightColumnValue;

    public RelationTableIdPair(String relationTable, Object leftColumnValue, Object rightColumnValue) {
        this.relationTable = relationTable;
        this.leftColumnValue = leftColumnValue;
        this.rightColumnValue = rightColumnValue;
    }

    public String getRelationTable() {
        return relationTable;
    }

    public Object getLeftColumnValue() {
        return leftColumnValue;
    }

    public Object getRightColumnValue() {
        return rightColumnValue;
    }

    @Override
    public boolean equals(Object o) {
        
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelationTableIdPair that = (RelationTableIdPair) o;

        if (relationTable != null ? !relationTable.equals(that.relationTable) : that.relationTable != null)
            return false;
        if (leftColumnValue != null ? !leftColumnValue.equals(that.leftColumnValue) : that.leftColumnValue != null)
            return false;
        return rightColumnValue != null ? rightColumnValue.equals(that.rightColumnValue) : that.rightColumnValue == null;

    }

    @Override
    public int hashCode() {
        int result = relationTable != null ? relationTable.hashCode() : 0;
        result = 31 * result + (leftColumnValue != null ? leftColumnValue.hashCode() : 0);
        result = 31 * result + (rightColumnValue != null ? rightColumnValue.hashCode() : 0);
        return result;
    }
}
