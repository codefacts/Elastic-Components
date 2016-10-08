package elasta.orm.jpa;

/**
 * Created by Jango on 10/8/2016.
 */
public class RelationTableColumns {
    private final String leftColumn;
    private final String rightColumn;

    public RelationTableColumns(String leftColumn, String rightColumn) {
        this.leftColumn = leftColumn;
        this.rightColumn = rightColumn;
    }

    public String getLeftColumn() {
        return leftColumn;
    }

    public String getRightColumn() {
        return rightColumn;
    }

    @Override
    public boolean equals(Object o) {
        
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelationTableColumns that = (RelationTableColumns) o;

        if (leftColumn != null ? !leftColumn.equals(that.leftColumn) : that.leftColumn != null) return false;
        return rightColumn != null ? rightColumn.equals(that.rightColumn) : that.rightColumn == null;

    }

    @Override
    public int hashCode() {
        int result = leftColumn != null ? leftColumn.hashCode() : 0;
        result = 31 * result + (rightColumn != null ? rightColumn.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RelationTableColumns{" +
            "leftColumn='" + leftColumn + '\'' +
            ", rightColumn='" + rightColumn + '\'' +
            '}';
    }
}
