package elasta.composer.validator;

import elasta.composer.util.Context;
import elasta.core.promise.intfs.Promise;

/**
 * Created by shahadat on 4/27/16.
 */
public interface ValidatorDeferred<T> {
    Promise<ValidationResult> validate(T val, Context context);
}
