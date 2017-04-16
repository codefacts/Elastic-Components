package elasta.orm.upsert;

import com.google.common.collect.ImmutableList;
import elasta.commons.Utils;
import elasta.orm.upsert.ex.TableDataException;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Jango on 2017-01-09.
 */
final public class TableData {
    final String table;
    final String[] primaryColumns;
    final JsonObject values;
    final int hash;

    private TableData(String table, String[] primaryColumns, Map<String, Object> values) {
        this.table = table;
        this.primaryColumns = primaryColumns;
        this.values = new JsonObject(Collections.unmodifiableMap(values));
        this.hash = calHashCode();
    }

    public TableData(String table, String[] primaryColumns, JsonObject values) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(primaryColumns);
        Objects.requireNonNull(values);
        if (primaryColumns.length <= 0) {
            throw new TableDataException("No primary column is given for table '" + table + "'");
        }
        values = new JsonObject(
            Collections.unmodifiableMap(
                values.getMap().entrySet().stream()
                    .filter(entry -> entry.getKey() != null)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            )
        );
        checkValueExistsForEachColumn(primaryColumns, values);
        this.table = table;
        this.primaryColumns = primaryColumns;
        this.values = values;
        this.hash = calHashCode();
    }

    private void checkValueExistsForEachColumn(String[] primaryColumns, JsonObject values) {
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
            "table='" + table + '\'' +
            ", primaryColumns=" + Arrays.toString(primaryColumns) +
            ", values=" + values +
            ", hash=" + hash +
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

    public TableData addValues(Map<String, Object> map) {

        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();

        hashMap.putAll(values.getMap());

        hashMap.putAll(mapEntries(map));

        return new TableData(
            table,
            primaryColumns,
            hashMap
        );
    }

    private Map<String, Object> mapEntries(Map<String, Object> map) {
        final Map<String, Object> newMap = new LinkedHashMap<>();
        final Map<String, Object> valuesMap = values.getMap();
        map.entrySet().stream()
            .filter(entry -> {

                    String key = entry.getKey();

                    if (key == null) {
                        return false;
                    }

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
            .forEach(entry -> newMap.put(entry.getKey(), entry.getValue()));

        return newMap;
    }
}
