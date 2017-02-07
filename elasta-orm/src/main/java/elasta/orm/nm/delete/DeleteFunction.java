package elasta.orm.nm.delete;

import elasta.orm.nm.delete.impl.DeleteData;
import elasta.orm.nm.upsert.TableData;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-25.
 */
@FunctionalInterface
public interface DeleteFunction {
    DeleteData delete(JsonObject entity, DeleteContext context);
}
