package elasta.orm.nm.upsert;

import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jango on 2017-01-21.
 */
public class UpsertContextImpl implements UpsertContext {
    final Map<String, TableData> map;

    public UpsertContextImpl() {
        this.map = new HashMap<>();
    }

    public UpsertContextImpl(Map<String, TableData> map) {
        this.map = map;
    }

    @Override
    public UpsertContext put(String tableAndPrimaryKey, TableData tableData) {
        map.put(tableAndPrimaryKey, tableData);
        return this;
    }

    @Override
    public UpsertContext putOrMerge(String tableAndPrimaryKey, TableData tableData) {
        if (map.containsKey(tableAndPrimaryKey)) {

            map.get(tableAndPrimaryKey).getValues().getMap().putAll(tableData.getValues().getMap());

            return this;
        }

        map.put(tableAndPrimaryKey, tableData);

        return this;
    }

    @Override
    public TableData get(String tableAndPrimaryKey) {
        TableData tableData = map.get(tableAndPrimaryKey);

        if (tableData == null) {
            throw new NullPointerException("No table data found for key '" + tableAndPrimaryKey + "'");
        }

        return tableData;
    }

    @Override
    public boolean exists(String tableAndPrimaryKey) {
        return map.containsKey(tableAndPrimaryKey);
    }
}
