package elasta.orm.nm.query.json.mapping;

import elasta.orm.nm.query.ParamsBuilder;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-07.
 */
public interface JsonQueryParser {
    String toSql(JsonObject query, ParamsBuilder paramsBuilder);
}
