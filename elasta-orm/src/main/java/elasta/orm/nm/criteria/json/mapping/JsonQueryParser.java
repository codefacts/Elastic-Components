package elasta.orm.nm.criteria.json.mapping;

import elasta.orm.nm.criteria.ParamsBuilder;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-07.
 */
public interface JsonQueryParser {
    String toSql(JsonObject query, ParamsBuilder paramsBuilder);
}
