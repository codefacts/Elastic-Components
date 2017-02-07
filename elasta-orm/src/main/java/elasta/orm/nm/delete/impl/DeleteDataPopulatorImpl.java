package elasta.orm.nm.delete.impl;

import elasta.orm.nm.delete.DeleteData;
import elasta.orm.nm.delete.DeleteDataPopulator;
import elasta.orm.nm.delete.FieldColumnMapping;
import elasta.orm.nm.delete.PrimaryColumnValuePair;
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
    final FieldColumnMapping[] primaryColumnMappings;

    public DeleteDataPopulatorImpl(String table, FieldColumnMapping[] primaryColumnMappings) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(primaryColumnMappings);
        this.table = table;
        this.primaryColumnMappings = primaryColumnMappings;
    }

    @Override
    public DeleteData populate(JsonObject jsonObject) {

        final List<PrimaryColumnValuePair> pairs = Arrays.asList(primaryColumnMappings)
            .stream()
            .map(mapping -> new PrimaryColumnValuePair(
                mapping.getColumn(),
                jsonObject.getValue(mapping.getField()))
            )
            .collect(Collectors.toList());

        return new DeleteData(table, pairs.toArray(new PrimaryColumnValuePair[pairs.size()]));
    }
}
