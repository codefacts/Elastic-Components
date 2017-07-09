package tracker.server.interceptors;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import lombok.Builder;
import lombok.Value;
import tracker.server.request.handlers.RequestHandler;

import java.util.Objects;

/**
 * Created by sohan on 7/3/2017.
 */
public interface AuthInterceptor extends RequestHandler {
    @Override
    void handle(RoutingContext ctx);

    @Value
    @Builder
    final class MethodAndUri {
        final HttpMethod httpMethod;
        final String uri;

        public MethodAndUri(HttpMethod httpMethod, String uri) {
            Objects.requireNonNull(httpMethod);
            Objects.requireNonNull(uri);
            this.httpMethod = httpMethod;
            this.uri = uri;
        }
    }
}
