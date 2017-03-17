package elasta.orm.delete;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 3/17/2017.
 */
public interface DeleteFunction {
    Promise<JsonObject> delete(JsonObject entity, DeleteContext deleteContext);
}
