package elasta.orm.upsert.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.upsert.DependencyColumnValuePopulator;
import elasta.orm.upsert.ForeignColumnMapping;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Jango on 2017-01-10.
 */
final public class DependencyColumnValuePopulatorImpl implements DependencyColumnValuePopulator {
    final ForeignColumnMapping[] foreignColumnMappings;

    public DependencyColumnValuePopulatorImpl(ForeignColumnMapping[] foreignColumnMappings) {
        Objects.requireNonNull(foreignColumnMappings);
        this.foreignColumnMappings = foreignColumnMappings;
    }

    @Override
    public JsonObject populate(JsonObject childTableData) {

        Objects.requireNonNull(childTableData);

        final ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.builder();

        for (ForeignColumnMapping foreignColumnMapping : foreignColumnMappings) {
            mapBuilder.put(
                foreignColumnMapping.getSrcColumn(),
                childTableData.getValue(
                    foreignColumnMapping.getDstColumn()
                )
            );
        }

        return new JsonObject(mapBuilder.build());
    }
}
