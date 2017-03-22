package elasta.orm.query.read.impl;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 17/02/12.
 */
public interface DirectRelationReader {
    JsonObject read(JsonArray data, List<JsonArray> dataList);
}
