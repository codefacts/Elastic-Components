package elasta.orm.delete.impl;

import elasta.orm.delete.ListTablesToDeleteContext;
import elasta.orm.delete.ListTablesToDeleteFunction;
import elasta.orm.delete.VirtualChildHandler;
import elasta.orm.upsert.ColumnToColumnMapping;
import io.vertx.core.json.JsonObject;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * Created by sohan on 3/12/2017.
 */
final public class VirtualChildHandlerImpl implements VirtualChildHandler {
    final String field;
    final ColumnToColumnMapping[] columnToColumnMappings;
    final ListTablesToDeleteFunction listTablesToDeleteFunction;

    public VirtualChildHandlerImpl(String field, ColumnToColumnMapping[] columnToColumnMappings, ListTablesToDeleteFunction listTablesToDeleteFunction) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(columnToColumnMappings);
        Objects.requireNonNull(listTablesToDeleteFunction);
        this.field = field;
        this.columnToColumnMappings = columnToColumnMappings;
        this.listTablesToDeleteFunction = listTablesToDeleteFunction;
    }

    @Override
    public String field() {
        return field;
    }

    @Override
    public void handle(JsonObject parentEntity, JsonObject childEntity, ListTablesToDeleteContext context) {

        childEntity = new JsonObject(new LinkedHashMap<>(childEntity.getMap()));

        for (ColumnToColumnMapping columnToColumnMapping : columnToColumnMappings) {
            childEntity.put(
                columnToColumnMapping.getSrcColumn(),
                parentEntity.getValue(
                    columnToColumnMapping.getDstColumn()
                )
            );
        }

        listTablesToDeleteFunction.listTables(childEntity, context);
    }
}
