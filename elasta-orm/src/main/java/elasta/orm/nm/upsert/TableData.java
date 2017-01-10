package elasta.orm.nm.upsert;

import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Jango on 2017-01-09.
 */
final public class TableData {
    final String table;
    final String[] primaryColumns;
    final JsonObject values;

    public TableData(String table, String[] primaryColumns, JsonObject values) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(primaryColumns);
        Objects.requireNonNull(values);
        this.table = table;
        this.primaryColumns = primaryColumns;
        this.values = values;
    }

    public String getTable() {
        return table;
    }

    public String[] getPrimaryColumns() {
        return primaryColumns;
    }

    public JsonObject getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableData tableData = (TableData) o;

        if (table != null ? !table.equals(tableData.table) : tableData.table != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(primaryColumns, tableData.primaryColumns)) return false;
        return values != null ? values.equals(tableData.values) : tableData.values == null;

    }

    @Override
    public int hashCode() {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(primaryColumns);
        result = 31 * result + (values != null ? values.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TableData{" +
            "table='" + table + '\'' +
            ", primaryColumns=" + Arrays.toString(primaryColumns) +
            ", values=" + values +
            '}';
    }
}
