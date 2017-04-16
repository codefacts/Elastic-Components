package elasta.orm.upsert.impl;

import elasta.orm.upsert.FieldToColumnMapping;
import elasta.orm.upsert.TableData;
import elasta.orm.upsert.TableDataPopulator;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 2017-01-10.
 */
final public class TableDataPopulatorImpl implements TableDataPopulator {
    private final String table;
    private final String primaryColumn;
    private final FieldToColumnMapping[] fieldToColumnMappings;

    public TableDataPopulatorImpl(String table, String primaryColumn, FieldToColumnMapping[] fieldToColumnMappings) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(primaryColumn);
        Objects.requireNonNull(fieldToColumnMappings);
        this.table = table;
        this.primaryColumn = primaryColumn;
        this.fieldToColumnMappings = fieldToColumnMappings;
    }

    @Override
    public TableData populate(JsonObject jsonObject) {

        Objects.requireNonNull(jsonObject);

        final Map<String, Object> map = new LinkedHashMap<>();

        for (FieldToColumnMapping fieldToColumnMapping : fieldToColumnMappings) {

            final String field = fieldToColumnMapping.getField();

            Object value = jsonObject.getValue(
                field
            );

            if (value == null) {
                continue;
            }

            map.put(
                fieldToColumnMapping.getColumn(),
                value
            );
        }

        return new TableData(table, new String[]{primaryColumn}, new JsonObject(map));
    }
}
