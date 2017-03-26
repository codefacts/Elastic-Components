package elasta.orm.upsert.impl;

import elasta.orm.upsert.FieldToColumnMapping;
import elasta.orm.upsert.TableData;
import elasta.orm.upsert.TableDataPopulator;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
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

        HashMap<String, Object> map = new HashMap<>();

        for (FieldToColumnMapping fieldToColumnMapping : fieldToColumnMappings) {

            final String field = fieldToColumnMapping.getField();

            if (not(jsonObject.containsKey(field))) {
                continue;
            }

            map.put(
                fieldToColumnMapping.getColumn(),
                jsonObject.getValue(
                    field
                )
            );
        }

        return new TableData(table, new String[]{primaryColumn}, new JsonObject(map));
    }
}
