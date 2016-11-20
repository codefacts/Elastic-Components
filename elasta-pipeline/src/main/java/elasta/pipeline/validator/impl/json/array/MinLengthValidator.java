package elasta.pipeline.validator.impl.json.array;

import com.google.common.collect.ImmutableList;
import elasta.pipeline.util.ErrorCodes;
import elasta.pipeline.validator.ValidationResult;
import elasta.pipeline.validator.ValidationResultBuilder;
import elasta.pipeline.validator.JsonArrayValidator;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created by shahadat on 3/1/16.
 */
public class MinLengthValidator implements JsonArrayValidator {
    private final int minLength;

    public MinLengthValidator(int minLength) {
        this.minLength = minLength;
    }

    @Override
    public List<ValidationResult> validate(JsonArray json) {

        if (json.size() < minLength) {
            return ImmutableList.of(invalidate(json));
        }

        return null;
    }

    private ValidationResult invalidate(JsonArray json) {
        return new ValidationResultBuilder()
            .setErrorCode(ErrorCodes.MAX_LENGTH_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("maxLength", minLength)
            )
            .createValidationResult();
    }
}
