package elasta.orm.idgenerator;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 6/28/2017.
 */
public interface ObjectIdGenerator<T> {

    Promise<JsonObject> generateId(String entity, JsonObject jsonObject);
}
