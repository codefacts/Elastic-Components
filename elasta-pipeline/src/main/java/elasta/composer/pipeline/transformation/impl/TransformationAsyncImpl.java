package elasta.composer.pipeline.transformation.impl;

import elasta.composer.pipeline.transformation.TransformationAsync;
import elasta.composer.pipeline.util.Context;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.List;

/**
 * Created by shahadat on 5/11/16.
 */
public class TransformationAsyncImpl<T, R> implements TransformationAsync<T, R> {
    private final List<TransformationAsync<Object, Object>> list;

    public TransformationAsyncImpl(List<TransformationAsync<Object, Object>> list) {
        this.list = list;
    }

    @Override
    public Promise<R> transform(T val, Context context) {

        try {

            if (list.size() <= 0) {
                return Promises.just((R) val);
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
            return Promises.error(ex);
        }
    }
}
