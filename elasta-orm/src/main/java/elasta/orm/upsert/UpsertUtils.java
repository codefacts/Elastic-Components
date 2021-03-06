package elasta.orm.upsert;

import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.core.RelationType;
import elasta.orm.entity.core.columnmapping.DirectRelationMapping;
import elasta.orm.entity.core.columnmapping.RelationMapping;
import elasta.orm.entity.core.columnmapping.RelationMappingOptions;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Jango on 2017-01-09.
 */
public interface UpsertUtils {
    static String toTableAndPrimaryColumnsKey(String table, String[] primaryColumns, JsonObject values) {

        return table +
            "[" +
            Arrays.stream(primaryColumns)
                .map(column -> column + ":" + values.getValue(column))
                .collect(Collectors.joining(",")) +
            "]";
    }

    static Stream<RelationMapping> getRelationMappingsForUpsert(DbMapping dbMapping) {
        return Arrays.stream(dbMapping.getRelationMappings())
            .filter(relationMapping -> {
                if (relationMapping.getColumnType() == RelationType.DIRECT) {
                    return true;
                }
                return relationMapping.getOptions().getCascadeUpsert() == RelationMappingOptions.CascadeUpsert.YES;
            });
    }
}
