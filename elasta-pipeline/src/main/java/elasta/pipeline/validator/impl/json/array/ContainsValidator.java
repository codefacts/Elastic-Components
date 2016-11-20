package elasta.pipeline.validator.impl.json.array;

import com.google.common.collect.ImmutableList;
import elasta.pipeline.util.ErrorCodes;
import elasta.pipeline.validator.JsonArrayValidator;
import elasta.pipeline.validator.ValidationResult;
import elasta.pipeline.validator.ValidationResultBuilder;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created by shahadat on 3/1/16.
 */
public class ContainsValidator<T> implements JsonArrayValidator {
    private final T value;

    public ContainsValidator(T value) {
        this.value = value;
    }

    @Override
    public List<ValidationResult> validate(JsonArray json) {

        if (!json.contains(value)) {
            return ImmutableList.of(invalidate(json));
        }

        return null;
    }

    private ValidationResult invalidate(JsonArray json) {
        return new ValidationResultBuilder()
            .setErrorCode(ErrorCodes.VALUE_MISSING_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("element", value)
            )
            .createValidationResult();
    }
}
