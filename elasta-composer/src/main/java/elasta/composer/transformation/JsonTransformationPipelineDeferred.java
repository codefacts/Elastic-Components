package elasta.composer.transformation;

import elasta.composer.util.Context;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by shahadat on 4/27/16.
 */
public class JsonTransformationPipelineDeferred implements TransformDeferred<JsonObject, JsonObject> {
    private final List<TransformDeferred<JsonObject, JsonObject>> list;

    public JsonTransformationPipelineDeferred(List<TransformDeferred<JsonObject, JsonObject>> list) {
        this.list = list;
    }

    @Override
    public Promise<JsonObject> transform(JsonObject val, Context context) {

        try {

            if (list.size() <= 0) {
                return Promises.just(val);
            }

            Promise<JsonObject> promise;
            promise = list.get(0).transform(val, context);

            final int ls = list.size();
            for (int i = 1; i < ls; i++) {
                final int idx = i;
                promise = promise.mapP(obj -> list.get(idx).transform(obj, context));
            }

            return promise;

        } catch (Exception ex) {
            return Promises.error(ex);
        }
    }
}
