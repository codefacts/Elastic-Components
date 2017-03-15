package elasta.orm.delete;

import elasta.sql.core.DeleteData;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-25.
 */
@FunctionalInterface
public interface DeleteFunction {
    DeleteData delete(JsonObject entity, DeleteContext context);
}
