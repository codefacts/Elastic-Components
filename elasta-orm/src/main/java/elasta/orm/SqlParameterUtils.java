package elasta.orm;

import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 9/15/2016.
 */
public interface SqlParameterUtils {
    String toSql(JsonObject criteria);
}
