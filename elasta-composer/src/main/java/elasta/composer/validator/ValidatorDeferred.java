package elasta.composer.validator;

import io.crm.promise.intfs.Promise;
import io.crm.util.Context;

/**
 * Created by shahadat on 4/27/16.
 */
public interface ValidatorDeferred<T> {
    Promise<ValidationResult> validate(T val, Context context);
}
