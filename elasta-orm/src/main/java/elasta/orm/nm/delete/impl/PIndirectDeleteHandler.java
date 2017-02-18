package elasta.orm.nm.delete.impl;

import elasta.orm.nm.delete.DeleteContext;
import elasta.orm.nm.upsert.TableData;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 17/02/16.
 */
public interface PIndirectDeleteHandler {
    void delete(TableData childEntity, DeleteContext context);
}
