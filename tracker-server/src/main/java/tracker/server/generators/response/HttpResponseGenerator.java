package tracker.server.generators.response;

import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 7/3/2017.
 */
@FunctionalInterface
public interface HttpResponseGenerator<T> {

    HttpResponse generate(T value);

    @Value
    @Builder
    final class HttpResponse {
        final int statusCode;
        final Map<String, String> headers;
        final String body;

        public HttpResponse(int statusCode, Map<String, String> headers, String body) {
            Objects.requireNonNull(statusCode);
            Objects.requireNonNull(headers);
            Objects.requireNonNull(body);
            this.statusCode = statusCode;
            this.headers = headers;
            this.body = body;
        }
    }
}
