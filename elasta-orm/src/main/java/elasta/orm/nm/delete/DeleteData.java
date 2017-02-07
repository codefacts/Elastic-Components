package elasta.orm.nm.delete;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Jango on 17/02/07.
 */
final public class DeleteData {
    final String table;
    final PrimaryColumnValuePair[] primaryColumnValuePairs;

    public DeleteData(String table, PrimaryColumnValuePair[] primaryColumnValuePairs) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(primaryColumnValuePairs);
        this.table = table;
        this.primaryColumnValuePairs = primaryColumnValuePairs;
    }

    public String getTable() {
        return table;
    }

    public PrimaryColumnValuePair[] getPrimaryColumnValuePairs() {
        return primaryColumnValuePairs;
    }

    @Override
    public String toString() {
        return "DeleteData{" +
            "table='" + table + '\'' +
            ", primaryColumnValuePairs=" + Arrays.toString(primaryColumnValuePairs) +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeleteData that = (DeleteData) o;

        if (table != null ? !table.equals(that.table) : that.table != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(primaryColumnValuePairs, that.primaryColumnValuePairs);

    }

    @Override
    public int hashCode() {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(primaryColumnValuePairs);
        return result;
    }
}
