package elasta.composer.util;

import elasta.core.promise.intfs.Defer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Created by Shahadat on 8/31/2016.
 */
public class Util {

    public static <T> Handler<AsyncResult<T>> makeDeferred(Defer<T> defer) {
        return r -> {
            if (r.failed()) defer.reject(r.cause());
            else defer.resolve(r.result());
        };
    }
}
