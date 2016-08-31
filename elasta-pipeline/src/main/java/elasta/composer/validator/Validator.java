package elasta.composer.validator;

/**
 * Created by shahadat on 2/28/16.
 */
public interface Validator<T> {
    ValidationResult validate(T val);
}
