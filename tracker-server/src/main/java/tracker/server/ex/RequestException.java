package tracker.server.ex;

import com.google.common.collect.ImmutableMap;
import elasta.composer.model.response.ErrorModel;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.Optional;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;

/**
 * Created by sohan on 7/3/2017.
 */
@Value
final public class RequestException extends RuntimeException {
    final int httpStatusCode;
    final String statusCode;
    final Object body;

    public RequestException(JsonObject jsonObject) {
        this(BAD_REQUEST.code(), jsonObject);
    }

    public RequestException(int httpStatusCode, JsonObject jsonObject) {
        this(httpStatusCode, jsonObject, null);
    }

    public RequestException(JsonObject jsonObject, Throwable throwable) {
        this(BAD_REQUEST.code(), jsonObject, throwable);
    }

    public RequestException(int httpStatusCode, JsonObject jsonObject, Throwable throwable) {
        this(httpStatusCode, jsonObject, throwable, null);
    }

    public RequestException(JsonObject jsonObject, Throwable throwable, Object body) {
        this(BAD_REQUEST.code(), jsonObject, throwable, body);
    }

    public RequestException(int httpStatusCode, JsonObject jsonObject, Throwable throwable, Object body) {
        super(jsonObject.getString(ErrorModel.message), throwable);

        this.httpStatusCode = httpStatusCode;

        Objects.requireNonNull(jsonObject.getString(ErrorModel.statusCode));
        Objects.requireNonNull(jsonObject.getString(ErrorModel.message));

        statusCode = jsonObject.getString(ErrorModel.statusCode);
        this.body = body == null ? null : body;
    }

    public RequestException(String statusCode, String message) {
        this(BAD_REQUEST.code(), statusCode, message);
    }

    public RequestException(int httpStatusCode, String statusCode, String message) {
        this(httpStatusCode, statusCode, message, null, null);
    }

    public RequestException(String statusCode, String message, Throwable throwable) {
        this(BAD_REQUEST.code(), statusCode, message, throwable);
    }

    public RequestException(int httpStatusCode, String statusCode, String message, Throwable throwable) {
        this(httpStatusCode, statusCode, message, throwable, null);
    }

    public RequestException(String statusCode, String message, Throwable throwable, Object body) {
        this(BAD_REQUEST.code(), statusCode, message, throwable, body);
    }

    public RequestException(int httpStatusCode, String statusCode, String message, Throwable throwable, Object body) {
        super(message, throwable);

        this.httpStatusCode = httpStatusCode;

        Objects.requireNonNull(statusCode);
        Objects.requireNonNull(message);

        this.statusCode = statusCode;
        this.body = body == null ? null : body;
    }

    public <T> Optional<T> getBody() {
        return Optional.ofNullable((T) body);
    }

    public JsonObject toJsonResponse() {
        return new JsonObject(ImmutableMap.of(
            ErrorModel.statusCode, statusCode,
            ErrorModel.message, getMessage()
        ));
    }
}
