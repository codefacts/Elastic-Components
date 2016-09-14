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
 * Created by Jango on 9/12/2016.
 */
public class SenderUtilsImpl implements SenderUtils {
    private final Vertx vertx;

    public SenderUtilsImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    public <T> Promise<T> send(String address, Object message) {

        Defer<T> defer = Promises.defer();

        vertx.eventBus().send(address, message, (AsyncResult<Message<T>> event) -> {
            if (event.failed()) {
                defer.reject(event.cause());
            } else {
                defer.resolve(event.result().body());
            }
        });

        return defer.promise();
    }

    public Promise<JsonObject> sendAndReceiveJsonObject(String address, Object jsonReq) {
        return send(address, jsonReq);
    }

    public Promise<JsonArray> sendAndReceiveJsonArray(String address, Object jsonReq) {
        return send(address, jsonReq);
    }
}
