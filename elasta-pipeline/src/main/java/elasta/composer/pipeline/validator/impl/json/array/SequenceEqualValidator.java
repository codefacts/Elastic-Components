package elasta.composer.pipeline.validator.impl.json.array;

import com.google.common.collect.ImmutableList;
import elasta.composer.pipeline.util.ErrorCodes;
import elasta.composer.pipeline.validator.JsonArrayValidator;
import elasta.composer.pipeline.validator.ValidationResult;
import elasta.composer.pipeline.validator.ValidationResultBuilder;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created by shahadat on 3/1/16.
 */
public class SequenceEqualValidator implements JsonArrayValidator {
    private final JsonArray jsonArray;

    public SequenceEqualValidator(JsonArray jsonArray) {
        requireNonNull(jsonArray);
        this.jsonArray = jsonArray;
    }

    @Override
    public List<ValidationResult> validate(JsonArray json) {

        for (int i = 0; i < json.size(); i++) {
            boolean equals = json.getValue(i).equals(jsonArray.getValue(i));
            if (!equals) {
                return ImmutableList.of(invalidate(json));
            }
        }

        return null;
    }

    private ValidationResult invalidate(JsonArray json) {
        return new ValidationResultBuilder()
            .setErrorCode(ErrorCodes.INVALID_SEQUENCE_ORDER_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("jsonArray", jsonArray)
            )
            .createValidationResult();
    }
}
