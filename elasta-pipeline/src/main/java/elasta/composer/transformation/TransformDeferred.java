package elasta.composer.transformation;

import elasta.composer.util.Context;
import elasta.core.promise.intfs.Promise;

/**
 * Created by shahadat on 4/27/16.
 */
public interface TransformDeferred<T, R> {
    Promise<R> transform(T val, Context context);
}
