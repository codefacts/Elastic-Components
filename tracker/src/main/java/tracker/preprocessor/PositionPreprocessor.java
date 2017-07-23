package tracker.preprocessor;

import elasta.composer.Msg;
import elasta.core.intfs.Fun2Async;
import elasta.core.intfs.Fun2Unckd;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 7/7/2017.
 */
public interface PositionPreprocessor<T> extends Fun2Async<Msg<T>, JsonObject, JsonObject> {
    @Override
    Promise<JsonObject> apply(Msg<T> msg, JsonObject jsonObject);
}
