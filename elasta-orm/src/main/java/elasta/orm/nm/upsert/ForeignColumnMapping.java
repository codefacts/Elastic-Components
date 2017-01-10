package elasta.orm.nm.upsert;

/**
 * Created by Jango on 2017-01-10.
 */
final public class ForeignColumnMapping {
    final String foreignColumn;
    final String column;

    public ForeignColumnMapping(String foreignColumn, String column) {
        this.foreignColumn = foreignColumn;
        this.column = column;
    }

    public String getForeignColumn() {
        return foreignColumn;
    }

    public String getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForeignColumnMapping that = (ForeignColumnMapping) o;

        if (foreignColumn != null ? !foreignColumn.equals(that.foreignColumn) : that.foreignColumn != null)
            return false;
        return column != null ? column.equals(that.column) : that.column == null;

    }

    @Override
    public int hashCode() {
        int result = foreignColumn != null ? foreignColumn.hashCode() : 0;
        result = 31 * result + (column != null ? column.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ForeignColumnMapping{" +
            "foreignColumn='" + foreignColumn + '\'' +
            ", column='" + column + '\'' +
            '}';
    }
}
