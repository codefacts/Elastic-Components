package elasta.pipeline.util;

/**
 * Created by shahadat on 2/28/16.
 */
public enum ErrorCodes {

    VALIDATION_ERROR(ErrorCodeHelper.validation(), "validation.error", ErrorCodeHelper.validationHttp()),
    INVALID_TYPE_VALIDATION_ERROR(ErrorCodeHelper.validation(), "invalid.type.validation.error", ErrorCodeHelper.validationHttp()),
    INVALID_RANGE_VALIDATION_ERROR(ErrorCodeHelper.validation(), "invalid.range.validation.error", ErrorCodeHelper.validationHttp()),
    NULL_VALIDATION_ERROR(ErrorCodeHelper.validation(), "null.validation.error", ErrorCodeHelper.validationHttp()),
    INVALID_VALUE_VALIDATION_ERROR(ErrorCodeHelper.validation(), "invalid.value.validation.error", ErrorCodeHelper.validationHttp()),
    PATTERN_VALIDATION_ERROR(ErrorCodeHelper.validation(), "pattern.validation.error", ErrorCodeHelper.validationHttp()),
    MAX_LENGTH_VALIDATION_ERROR(ErrorCodeHelper.validation(), "max.length.validation.error", ErrorCodeHelper.validationHttp()),
    MIN_LENGTH_VALIDATION_ERROR(ErrorCodeHelper.validation(), "min.length.validation.error", ErrorCodeHelper.validationHttp()),
    LENGTH_VALIDATION_ERROR(ErrorCodeHelper.validation(), "length.validation.error", ErrorCodeHelper.validationHttp()),
    INVALID_EMAIL_VALIDATION_ERROR(ErrorCodeHelper.validation(), "invalid.email.validation.error", ErrorCodeHelper.validationHttp()),
    INVALID_PHONE_VALIDATION_ERROR(ErrorCodeHelper.validation(), "invalid.phone.validation.error", ErrorCodeHelper.validationHttp()),
    VALUE_MISSING_VALIDATION_ERROR(ErrorCodeHelper.validation(), "value.missing.validation.error", ErrorCodeHelper.validationHttp()),
    INVALID_SEQUENCE_ORDER_VALIDATION_ERROR(ErrorCodeHelper.validation(), "invalid.sequence.order.validation.error", ErrorCodeHelper.validationHttp()),
    SERVER_ERROR(ErrorCodeHelper.error(), "server.error", ErrorCodeHelper.errorHttp()),
    NOT_FOUND(ErrorCodeHelper.error(), "not_found.error", ErrorCodeHelper.errorHttp()),
    POSITIVE_NUMBER_VALIDATION_ERROR(ErrorCodeHelper.validation(), "positive.number.validation.error", ErrorCodeHelper.validationHttp()),
    NON_ZERO_NUMBER_VALIDATION_ERROR(ErrorCodeHelper.validation(), "non_zero.number.validation.error", ErrorCodeHelper.validationHttp()),
    PASSWORD_MISMATCH(ErrorCodeHelper.validation(), "password.mismatch.validation.error", ErrorCodeHelper.validationHttp());

    private final int code;
    private final String messageCode;
    private final int httpResponseCode;

    ErrorCodes(int code, String messageCode, int httpResponseCode) {
        this.code = code;
        this.messageCode = messageCode;
        this.httpResponseCode = httpResponseCode;
    }

    public int code() {
        return code;
    }

    public String messageCode() {
        return messageCode;
    }

    public int httpResponseCode() {
        return httpResponseCode;
    }
}

