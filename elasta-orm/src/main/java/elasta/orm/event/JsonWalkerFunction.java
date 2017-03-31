package elasta.orm.event;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 3/29/2017.
 */
public interface JsonWalkerFunction {
    Promise<JsonObject> apply(JsonObject object, int index);
}
