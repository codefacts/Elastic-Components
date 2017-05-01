package elasta.orm.query.read;

import com.google.common.collect.ImmutableMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.List;

/**
 * Created by Jango on 17/02/12.
 */
public interface ObjectReader {

    JsonObject read(JsonArray data, List<JsonArray> dataList);
}
