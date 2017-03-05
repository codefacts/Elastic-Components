package elasta.orm.jpa;

/**
 * Created by Jango on 10/7/2016.
 */
public class TablePrimary {
    private final String table;
    private final String primaryKey;

    public TablePrimary(String table, String primaryKey) {
        this.table = table;
        this.primaryKey = primaryKey;
    }

    public String getTable() {
        return table;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TablePrimary that = (TablePrimary) o;

        if (table != null ? !table.equals(that.table) : that.table != null) return false;
        return primaryKey != null ? primaryKey.equals(that.primaryKey) : that.primaryKey == null;

    }

    @Override
    public int hashCode() {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + (primaryKey != null ? primaryKey.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TablePrimary{" +
            "dependentTable='" + table + '\'' +
            ", primaryKey='" + primaryKey + '\'' +
            '}';
    }
}
