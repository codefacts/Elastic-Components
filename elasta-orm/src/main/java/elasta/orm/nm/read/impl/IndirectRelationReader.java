package elasta.orm.nm.read.impl;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 17/02/12.
 */
public interface IndirectRelationReader {

    List<JsonObject> read(JsonArray data, List<JsonArray> dataList);
}
