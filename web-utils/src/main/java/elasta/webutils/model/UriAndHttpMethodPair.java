package elasta.webutils.model;

import io.vertx.core.http.HttpMethod;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 5/10/2017.
 */
@Value
@Builder
public final class UriAndHttpMethodPair {
    final String uri;
    final HttpMethod httpMethod;

    UriAndHttpMethodPair(String uri, HttpMethod httpMethod) {
        Objects.requireNonNull(uri);
        Objects.requireNonNull(httpMethod);
        this.uri = uri;
        this.httpMethod = httpMethod;
    }
}
