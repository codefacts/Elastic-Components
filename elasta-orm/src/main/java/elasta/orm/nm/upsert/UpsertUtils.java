package elasta.orm.nm.upsert;

import io.vertx.core.json.JsonObject;

import java.util.Arrays;
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
}
