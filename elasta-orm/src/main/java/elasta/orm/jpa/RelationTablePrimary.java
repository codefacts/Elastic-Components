package elasta.orm.jpa;

/**
 * Created by Jango on 10/8/2016.
 */
public class RelationTablePrimary {
    private final String relationTable;
    private final String leftColumn;
    private final String rightColumn;

    public RelationTablePrimary(String relationTable, String leftColumn, String rightColumn) {
        this.relationTable = relationTable;
        this.leftColumn = leftColumn;
        this.rightColumn = rightColumn;
    }

    public String getRelationTable() {
        return relationTable;
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

        RelationTablePrimary that = (RelationTablePrimary) o;

        if (relationTable != null ? !relationTable.equals(that.relationTable) : that.relationTable != null)
            return false;
        if (leftColumn != null ? !leftColumn.equals(that.leftColumn) : that.leftColumn != null) return false;
        return rightColumn != null ? rightColumn.equals(that.rightColumn) : that.rightColumn == null;

    }

    @Override
    public int hashCode() {
        int result = relationTable != null ? relationTable.hashCode() : 0;
        result = 31 * result + (leftColumn != null ? leftColumn.hashCode() : 0);
        result = 31 * result + (rightColumn != null ? rightColumn.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RelationTablePrimary{" +
            "relationTable='" + relationTable + '\'' +
            ", leftColumn='" + leftColumn + '\'' +
            ", rightColumn='" + rightColumn + '\'' +
            '}';
    }
}
