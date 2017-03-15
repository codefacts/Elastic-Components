package elasta.orm.delete.impl;

import elasta.orm.delete.DeleteDataPopulator;
import elasta.sql.core.ColumnValuePair;
import elasta.sql.core.DeleteData;
import elasta.orm.upsert.FieldToColumnMapping;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Jango on 17/02/07.
 */
final public class DeleteDataPopulatorImpl implements DeleteDataPopulator {
    final String table;
    final FieldToColumnMapping[] primaryColumnMappings;

    public DeleteDataPopulatorImpl(String table, FieldToColumnMapping[] primaryColumnMappings) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(primaryColumnMappings);
        this.table = table;
        this.primaryColumnMappings = primaryColumnMappings;
    }

    @Override
    public DeleteData populate(JsonObject jsonObject) {

        final List<ColumnValuePair> pairs = Arrays.asList(primaryColumnMappings)
            .stream()
            .map(mapping -> new ColumnValuePair(
                mapping.getColumn(),
                jsonObject.getValue(mapping.getField()))
            )
            .collect(Collectors.toList());

        return new DeleteData(table, pairs.toArray(new ColumnValuePair[pairs.size()]));
    }
}
