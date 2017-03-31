package elasta.orm.upsert;

import elasta.orm.entity.core.ColumnType;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.core.columnmapping.DbColumnMapping;
import elasta.orm.entity.core.columnmapping.RelationMapping;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jango on 2017-01-09.
 */
public interface UpsertUtils {
    static String toTableAndPrimaryColumnsKey(String table, String[] primaryColumns, JsonObject values) {

        return table +
            "[" +
            Arrays.asList(primaryColumns).stream()
                .map(column -> column + ":" + values.getValue(column))
                .collect(Collectors.joining(",")) +
            "]";
    }

    static List<RelationMapping> getRelationMappingsForUpsert(DbMapping dbMapping) {
        return Arrays.stream(dbMapping.getDbColumnMappings()).filter(dbColumnMapping -> dbColumnMapping instanceof RelationMapping).map(dbColumnMapping -> (RelationMapping) dbColumnMapping).collect(Collectors.toList());
    }
}
