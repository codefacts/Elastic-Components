package elasta.pipeline.validator.impl.type;

import com.google.common.collect.ImmutableList;
import elasta.pipeline.util.ErrorCodes;
import elasta.pipeline.validator.JsonObjectValidator;
import elasta.pipeline.validator.ValidationResult;
import elasta.pipeline.validator.ValidationResultBuilder;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by shahadat on 3/1/16.
 */
public class TypeValidator implements JsonObjectValidator {
    public static final String TYPE = "type";
    private final String field;
    private final Consumer<JsonObject> check;
    private final Function<ValidationResult, ValidationResult> onValidate;

    public TypeValidator(String field, Consumer<JsonObject> check, Function<ValidationResult, ValidationResult> onValidate) {
        this.field = field;
        this.check = check;
        this.onValidate = onValidate;
    }

    @Override
    public List<ValidationResult> validate(JsonObject json) {
        try {
            check.accept(json);
        } catch (Exception e) {
            return ImmutableList.of(invalidate(json));
        }
        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return onValidate.apply(new ValidationResultBuilder()
            .setField(field)
            .setValue(json.getValue(field))
            .setErrorCode(ErrorCodes.INVALID_TYPE_VALIDATION_ERROR.code())
            .createValidationResult());
    }
}
