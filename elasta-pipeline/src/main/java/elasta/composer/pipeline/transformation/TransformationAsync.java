package elasta.composer.pipeline.transformation;

import elasta.composer.pipeline.util.Context;
import elasta.core.promise.intfs.Promise;

/**
 * Created by shahadat on 4/27/16.
 */
public interface TransformationAsync<T, R> {
    Promise<R> transform(T val, Context context);
}
