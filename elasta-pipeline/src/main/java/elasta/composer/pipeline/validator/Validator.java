package elasta.composer.pipeline.validator;

import java.util.List;

/**
 * Created by shahadat on 2/28/16.
 */
public interface Validator<T> {
    List<ValidationResult> validate(T val);
}
