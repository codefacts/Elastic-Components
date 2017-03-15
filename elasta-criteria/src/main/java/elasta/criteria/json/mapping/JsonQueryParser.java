package elasta.criteria.json.mapping;

import elasta.criteria.ParamsBuilder;import elasta.criteria.ParamsBuilder;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-07.
 */
public interface JsonQueryParser {
    String toSql(JsonObject query, ParamsBuilder paramsBuilder);
}
