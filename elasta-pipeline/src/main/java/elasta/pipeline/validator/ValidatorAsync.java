package elasta.pipeline.validator;

import elasta.core.promise.intfs.Promise;

import java.util.List;

/**
 * Created by shahadat on 4/27/16.
 */
public interface ValidatorAsync<T> {
    Promise<List<ValidationResult>> validate(T val);
}
