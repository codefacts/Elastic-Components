package elasta.composer.transformation;

import io.crm.promise.Promises;
import io.crm.promise.intfs.Promise;
import io.crm.util.Context;

import java.util.List;

/**
 * Created by shahadat on 5/11/16.
 */
public class TransformationPipelineDeferred<T, R> implements TransformDeferred<T, R> {
    private final List<TransformDeferred<Object, Object>> list;

    public TransformationPipelineDeferred(List<TransformDeferred<Object, Object>> list) {
        this.list = list;
    }

    @Override
    public Promise<R> transform(T val, Context context) {

        try {

            if (list.size() <= 0) {
                return Promises.from((R) val);
            }

            Promise<Object> promise;
            promise = list.get(0).transform(val, context);

            final int ls = list.size();
            for (int i = 1; i < ls; i++) {
                final int idx = i;
                promise = promise.mapP(
                    obj -> list.get(idx).transform(obj, context));
            }

            return (Promise<R>) promise;

        } catch (Exception ex) {
            return Promises.fromError(ex);
        }
    }
}
