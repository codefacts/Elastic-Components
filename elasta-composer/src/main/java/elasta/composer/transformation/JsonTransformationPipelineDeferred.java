package elasta.composer.transformation;

import io.crm.promise.Promises;
import io.crm.promise.intfs.Promise;
import io.crm.util.Context;
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
                return Promises.from(val);
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
            return Promises.fromError(ex);
        }
    }
}
