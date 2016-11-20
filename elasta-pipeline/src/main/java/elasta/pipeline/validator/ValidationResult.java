package elasta.pipeline.validator;

import io.vertx.core.json.JsonObject;

/**
 * Created by shahadat on 2/28/16.
 */
public class ValidationResult<T> {
    public static final String MESSAGE = "message";
    private final String field;
    private final T value;
    private final int errorCode;
    private final JsonObject additionals;

    ValidationResult(String fieldName, T value, int errorCode, JsonObject additionals) {
        this.field = fieldName;
        this.value = value;
        this.errorCode = errorCode;
        this.additionals = additionals;
    }

    public String getField() {
        return or(field, "");
    }

    private <T> T or(T field, T s) {
        return field == null ? s : field;
    }

    public T getValue() {
        return value;
    }

    public T getValue(T defaultVal) {
        return or(value, defaultVal);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public JsonObject getAdditionals() {
        return or(additionals, new JsonObject());
    }

    public JsonObject toJson() {
        return
            additionals
                .put("field", field)
                .put("value", value)
                .put("errorCode", errorCode)
            ;
    }

    public ValidationResult addAdditionals(JsonObject additionalsData) {
        additionalsData.getMap().forEach((k, v) -> additionals.put(k, v));
        return this;
    }

    public void message(String message) {
        additionals.put(MESSAGE, message);
    }

    @Override
    public String toString() {
        return toJson().encode();
    }
}
