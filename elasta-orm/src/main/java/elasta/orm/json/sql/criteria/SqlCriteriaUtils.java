package elasta.orm.json.sql.criteria;

import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 9/15/2016.
 */
public interface SqlCriteriaUtils {
    String toSql(JsonObject criteria);
}
