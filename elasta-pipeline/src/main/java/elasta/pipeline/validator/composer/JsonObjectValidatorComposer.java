package elasta.pipeline.validator.composer;

import elasta.pipeline.validator.JsonObjectValidator;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by shahadat on 2/28/16.
 */
public class JsonObjectValidatorComposer {
    private final List<JsonObjectValidator> validatorList;

    public JsonObjectValidatorComposer(List<JsonObjectValidator> validatorList) {
        Objects.requireNonNull(validatorList);
        this.validatorList = validatorList;
    }

    public JsonObjectValidatorComposer field(String field, Consumer<FieldValidatorComposer> consumer) {
        consumer.accept(new FieldValidatorComposer(field, validatorList));
        return this;
    }

    public List<JsonObjectValidator> getValidatorList() {
        return validatorList;
    }
}
