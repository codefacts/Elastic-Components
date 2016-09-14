package elasta.vertxutils;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Defer;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 9/11/2016.
 */
public interface SenderUtils {

    <T> Promise<T> send(String address, Object message);

    Promise<JsonObject> sendAndReceiveJsonObject(String address, Object jsonReq);

    Promise<JsonArray> sendAndReceiveJsonArray(String address, Object jsonReq);
}
