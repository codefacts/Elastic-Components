package elasta.vertxutils;

import elasta.core.promise.intfs.Defer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Created by Jango on 10/12/2016.
 */
public class VertxUtils {
    public static <T> Handler<AsyncResult<T>> deferred(Defer defer) {
        return event -> {
            if (event.failed()) {
                defer.reject(event.cause());
            } else {
                defer.resolve(event.result());
            }
        };
    }
}
