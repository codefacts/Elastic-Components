package elasta.composer.pipeline.validator.impl.json.array;

import com.google.common.collect.ImmutableList;
import elasta.composer.pipeline.util.ErrorCodes;
import elasta.composer.pipeline.validator.ValidationResult;
import elasta.composer.pipeline.validator.ValidationResultBuilder;
import elasta.composer.pipeline.validator.JsonArrayValidator;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created by shahadat on 3/1/16.
 */
public class ContainsAllValidator implements JsonArrayValidator {
    private final JsonArray jsonArray;

    public ContainsAllValidator(JsonArray jsonArray) {
        requireNonNull(jsonArray);
        this.jsonArray = jsonArray;
    }

    @Override
    public List<ValidationResult> validate(JsonArray json) {

        if (!json.getList().containsAll(jsonArray.getList())) {
            return ImmutableList.of(invalidate(json));
        }

        return null;
    }

    private ValidationResult invalidate(JsonArray json) {
        return new ValidationResultBuilder()
            .setErrorCode(ErrorCodes.VALUE_MISSING_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("jsonArray", jsonArray)
            )
            .createValidationResult();
    }
}
