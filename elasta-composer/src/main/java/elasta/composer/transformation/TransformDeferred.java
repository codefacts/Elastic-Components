package elasta.composer.transformation;

import io.crm.promise.intfs.Promise;
import io.crm.util.Context;

/**
 * Created by shahadat on 4/27/16.
 */
public interface TransformDeferred<T, R> {
    Promise<R> transform(T val, Context context);
}
