package elasta.composer.validator;

import io.vertx.core.json.JsonObject;

public class ValidationResultBuilder {
    private String fieldName;
    private Object value;
    private int errorCode;
    private JsonObject additionals;

    public ValidationResultBuilder setField(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public ValidationResultBuilder setValue(Object value) {
        this.value = value;
        return this;
    }

    public ValidationResultBuilder setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public ValidationResultBuilder setAdditionals(JsonObject additionals) {
        this.additionals = additionals;
        return this;
    }

    public ValidationResult createValidationResult() {
        return new ValidationResult(fieldName, value, errorCode, additionals == null ? new JsonObject() : additionals);
    }
}