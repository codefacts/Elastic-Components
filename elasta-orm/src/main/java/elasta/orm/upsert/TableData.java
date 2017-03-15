package elasta.orm.upsert;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.orm.upsert.ex.TableDataException;
import elasta.orm.upsert.ex.TableDataException;import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Jango on 2017-01-09.
 */
final public class TableData {
    final String table;
    final String[] primaryColumns;
    final JsonObject values;
    final int hash;

    public TableData(String table, String[] primaryColumns, JsonObject values) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(primaryColumns);
        Objects.requireNonNull(values);
        values = new JsonObject(
            ImmutableMap.copyOf(
                values.getMap()
            )
        );
        checkPrimaryColumnValuesGivenForEachColumn(primaryColumns, values);
        this.table = table;
        this.primaryColumns = primaryColumns;
        this.values = values;
        this.hash = calHashCode();
    }

    private void checkPrimaryColumnValuesGivenForEachColumn(String[] primaryColumns, JsonObject values) {
        for (String primaryColumn : primaryColumns) {
            if (values.getValue(primaryColumn) == null) {
                throw new TableDataException("No value is given for primary column '" + table + "." + primaryColumn + "'");
            }
        }
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

        if (!table.equals(tableData.table)) return false;
        if (!Arrays.equals(primaryColumns, tableData.primaryColumns)) return false;
        for (String primaryColumn : primaryColumns) {
            boolean equals = values.getValue(primaryColumn).equals(
                tableData.getValues().getValue(primaryColumn)
            );
            if (Utils.not(equals)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return "TableData{" +
            "dependentTable='" + table + '\'' +
            ", primaryColumns=" + Arrays.toString(primaryColumns) +
            ", values=" + values +
            '}';
    }

    private int calHashCode() {
        int result = table.hashCode();
        result = 31 * result + Arrays.hashCode(primaryColumns);

        ImmutableList.Builder<Object> listBuilder = ImmutableList.builder();
        for (String primaryColumn : primaryColumns) {
            listBuilder.add(values.getValue(primaryColumn));
        }

        result = 31 * result + listBuilder.build().hashCode();
        return result;
    }
}
