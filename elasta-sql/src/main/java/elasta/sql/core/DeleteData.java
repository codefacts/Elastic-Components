package elasta.sql.core;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Jango on 17/02/07.
 */
final public class DeleteData {
    final String table;
    final ColumnValuePair[] columnValuePairs;

    public DeleteData(String table, ColumnValuePair[] columnValuePairs) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(columnValuePairs);
        this.table = table;
        this.columnValuePairs = columnValuePairs;
    }

    public String getTable() {
        return table;
    }

    public ColumnValuePair[] getColumnValuePairs() {
        return columnValuePairs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeleteData that = (DeleteData) o;

        if (table != null ? !table.equals(that.table) : that.table != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(columnValuePairs, that.columnValuePairs);

    }

    @Override
    public int hashCode() {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(columnValuePairs);
        return result;
    }

    @Override
    public String toString() {
        return "DeleteData{" +
            "dependentTable='" + table + '\'' +
            ", columnValuePairs=" + Arrays.toString(columnValuePairs) +
            '}';
    }
}
