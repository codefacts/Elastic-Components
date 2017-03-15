package elasta.orm.upsert.impl;

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
    public JsonObject populate(JsonObject dependencyTableData) {

        final HashMap<String, Object> map = new HashMap<>();

        for (ForeignColumnMapping foreignColumnMapping : foreignColumnMappings) {
            map.put(
                foreignColumnMapping.getSrcColumn(),
                dependencyTableData.getValue(
                    foreignColumnMapping.getDstColumn()
                )
            );
        }

        return new JsonObject(map);
    }
}
