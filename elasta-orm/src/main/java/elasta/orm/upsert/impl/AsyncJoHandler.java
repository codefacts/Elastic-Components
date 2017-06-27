package elasta.orm.upsert.impl;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 6/27/2017.
 */
public interface AsyncJoHandler<R> {
    Promise<R> handle(JsonObject jsonObject);
}
