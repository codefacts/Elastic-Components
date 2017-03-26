package elasta.orm.upsert;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Jango on 2017-01-21.
 */
public class UpsertContextImpl implements UpsertContext {
    final Map<String, TableData> map;

    public UpsertContextImpl() {
        this(new LinkedHashMap<>());
    }

    public UpsertContextImpl(Map<String, TableData> map) {
        Objects.requireNonNull(map);
        this.map = map;
    }

    @Override
    public UpsertContext put(String tableAndPrimaryKey, TableData tableData) {
        map.put(tableAndPrimaryKey, tableData);
        return this;
    }

    @Override
    public UpsertContext putOrMerge(String tableAndPrimaryKey, TableData tableData) {

        tableData.getValues().forEach(stringObjectEntry -> {
            Objects.requireNonNull(stringObjectEntry.getValue(), "Null value provided as value for key '" + stringObjectEntry.getKey() + "' dependentTable: '" + tableData.table + "'");
        });

        if (map.containsKey(tableAndPrimaryKey)) {

            TableData data = map.get(tableAndPrimaryKey);

            map.put(tableAndPrimaryKey, data.withValues(
                tableData.getValues().getMap()
            ));

            return this;
        }

        map.put(tableAndPrimaryKey, tableData);

        return this;
    }

    @Override
    public TableData get(String tableAndPrimaryKey) {
        TableData tableData = map.get(tableAndPrimaryKey);

        if (tableData == null) {
            throw new NullPointerException("No dependentTable data found for key '" + tableAndPrimaryKey + "'");
        }

        return tableData;
    }

    @Override
    public boolean exists(String tableAndPrimaryKey) {
        return map.containsKey(tableAndPrimaryKey);
    }
}
