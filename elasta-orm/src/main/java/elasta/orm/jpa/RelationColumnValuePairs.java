package elasta.orm.jpa;

/**
 * Created by Jango on 10/8/2016.
 */
public class RelationColumnValuePairs {
    private final Object leftColumnValue;
    private final Object rightColumnValue;

    public RelationColumnValuePairs(Object leftColumnValue, Object rightColumnValue) {
        this.leftColumnValue = leftColumnValue;
        this.rightColumnValue = rightColumnValue;
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

        RelationColumnValuePairs that = (RelationColumnValuePairs) o;

        if (leftColumnValue != null ? !leftColumnValue.equals(that.leftColumnValue) : that.leftColumnValue != null)
            return false;
        return rightColumnValue != null ? rightColumnValue.equals(that.rightColumnValue) : that.rightColumnValue == null;

    }

    @Override
    public int hashCode() {
        int result = leftColumnValue != null ? leftColumnValue.hashCode() : 0;
        result = 31 * result + (rightColumnValue != null ? rightColumnValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RelationColumnValuePairs{" +
            "leftColumnValue='" + leftColumnValue + '\'' +
            ", rightColumnValue='" + rightColumnValue + '\'' +
            '}';
    }
}
