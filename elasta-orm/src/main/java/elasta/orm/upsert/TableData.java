package elasta.orm.upsert;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.orm.upsert.ex.TableDataException;
import io.vertx.core.json.JsonObject;
import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Jango on 2017-01-09.
 */
@Value
final public class TableData {
    final String table;
    final String[] primaryColumns;
    final JsonObject values;
    final boolean isNew;
    final int hash;

    private TableData(String table, String[] primaryColumns, Map<String, Object> values, boolean isNew) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(primaryColumns);
        Objects.requireNonNull(values);
        this.table = table;
        this.primaryColumns = primaryColumns;
        this.values = new JsonObject(values);
        this.isNew = isNew;
        this.hash = calHashCode();
    }

    public TableData(String table, String[] primaryColumns, JsonObject values) {
        this(table, primaryColumns, values, false);
    }

    public TableData(String table, String[] primaryColumns, JsonObject values, boolean isNew) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(primaryColumns);
        Objects.requireNonNull(values);

        if (primaryColumns.length <= 0) {
            throw new TableDataException("No primary column is given for table '" + table + "'");
        }

        values = new JsonObject(
            ImmutableMap.copyOf(
                values.getMap().entrySet().stream().filter(entry -> entry.getValue() != null).collect(Collectors.toList())
            )
        );

        this.table = table;
        this.primaryColumns = primaryColumns;
        this.values = values;
        this.isNew = isNew;

        checkValueExistsForEachColumn(primaryColumns, values);

        this.hash = calHashCode();
    }

    private void checkValueExistsForEachColumn(String[] primaryColumns, JsonObject values) {
        for (String primaryColumn : primaryColumns) {
            if (values.getValue(primaryColumn) == null) {
                throw new TableDataException("No value is given for primary column '" + table + "." + primaryColumn + "'");
            }
        }
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

    public TableData addValues(Map<String, Object> map) {
        return new TableData(
            table,
            primaryColumns,
            ImmutableMap.<String, Object>builder().putAll(this.values.getMap()).putAll(mapEntries(map)).build(),
            isNew
        );
    }

    public TableData withIsNew(boolean isNew) {
        return new TableData(
            table,
            primaryColumns,
            values.getMap(),
            isNew
        );
    }

    private Map<String, Object> mapEntries(Map<String, Object> map) {
        final ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.builder();
        Map<String, Object> valuesMap = values.getMap();
        map.entrySet().stream()
            .filter(entry -> {

                    String key = entry.getKey();

                    Objects.requireNonNull(key);

                    if (valuesMap.containsKey(key)) {
                        Object prevValue = valuesMap.get(key);

                        boolean isEquals = (prevValue == entry.getValue())
                            || (prevValue != null && prevValue.equals(entry.getValue()));

                        if (Utils.not(isEquals)) {
                            throw new TableDataException("PrevValue '" + prevValue + "' and newValue '" + entry.getValue() + "' must be equals for key '" + key + "'");
                        }
                        return false;
                    }

                    return true;
                }
            )
            .forEach(entry -> mapBuilder.put(entry.getKey(), entry.getValue()));

        return mapBuilder.build();
    }
}
