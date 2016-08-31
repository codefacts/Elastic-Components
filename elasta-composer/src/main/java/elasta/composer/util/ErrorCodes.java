package elasta.composer.util;

import static io.crm.ErrorCodeHelper.*;

/**
 * Created by shahadat on 2/28/16.
 */
public enum ErrorCodes {

    VALIDATION_ERROR(validation(), "validation.error", validationHttp()),
    INVALID_TYPE_VALIDATION_ERROR(validation(), "invalid.type.validation.error", validationHttp()),
    INVALID_RANGE_VALIDATION_ERROR(validation(), "invalid.range.validation.error", validationHttp()),
    NULL_VALIDATION_ERROR(validation(), "null.validation.error", validationHttp()),
    INVALID_VALUE_VALIDATION_ERROR(validation(), "invalid.value.validation.error", validationHttp()),
    PATTERN_VALIDATION_ERROR(validation(), "pattern.validation.error", validationHttp()),
    MAX_LENGTH_VALIDATION_ERROR(validation(), "max.length.validation.error", validationHttp()),
    MIN_LENGTH_VALIDATION_ERROR(validation(), "min.length.validation.error", validationHttp()),
    LENGTH_VALIDATION_ERROR(validation(), "length.validation.error", validationHttp()),
    INVALID_EMAIL_VALIDATION_ERROR(validation(), "invalid.email.validation.error", validationHttp()),
    INVALID_PHONE_VALIDATION_ERROR(validation(), "invalid.phone.validation.error", validationHttp()),
    VALUE_MISSING_VALIDATION_ERROR(validation(), "value.missing.validation.error", validationHttp()),
    INVALID_SEQUENCE_ORDER_VALIDATION_ERROR(validation(), "invalid.sequence.order.validation.error", validationHttp()),
    SERVER_ERROR(error(), "server.error", errorHttp()),
    NOT_FOUND(error(), "not_found.error", errorHttp()),
    POSITIVE_NUMBER_VALIDATION_ERROR(validation(), "positive.number.validation.error", validationHttp()),
    NON_ZERO_NUMBER_VALIDATION_ERROR(validation(), "non_zero.number.validation.error", validationHttp()),
    PASSWORD_MISMATCH(validation(), "password.mismatch.validation.error", validationHttp());

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

